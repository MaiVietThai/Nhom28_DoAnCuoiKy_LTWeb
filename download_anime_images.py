import json
import re
import time
import urllib.parse
import urllib.request
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent
IMAGE_DIR = (
    BASE_DIR
    / "src"
    / "main"
    / "resources"
    / "static"
    / "images"
    / "products"
)

IMAGE_DIR.mkdir(parents=True, exist_ok=True)

CHARACTERS = [
    "Monkey D. Luffy", "Roronoa Zoro", "Nami", "Usopp",
    "Sanji", "Tony Tony Chopper", "Nico Robin", "Franky",
    "Brook", "Jinbe",

    "Naruto Uzumaki", "Sasuke Uchiha", "Sakura Haruno",
    "Kakashi Hatake", "Itachi Uchiha", "Madara Uchiha",
    "Hinata Hyuga", "Minato Namikaze", "Gaara", "Jiraiya",

    "Son Goku", "Vegeta", "Son Gohan", "Piccolo", "Trunks",
    "Frieza", "Cell", "Majin Buu", "Broly", "Future Trunks",

    "Tanjiro Kamado", "Nezuko Kamado", "Zenitsu Agatsuma",
    "Inosuke Hashibira", "Kyojuro Rengoku", "Giyu Tomioka",
    "Shinobu Kocho", "Tengen Uzui", "Mitsuri Kanroji",
    "Muichiro Tokito",

    "Satoru Gojo", "Yuji Itadori", "Megumi Fushiguro",
    "Nobara Kugisaki", "Ryomen Sukuna", "Yuta Okkotsu",
    "Suguru Geto", "Kento Nanami", "Toji Fushiguro",
    "Maki Zenin",

    "Eren Yeager", "Mikasa Ackerman", "Armin Arlert",
    "Levi Ackerman", "Erwin Smith", "Hange Zoe",
    "Reiner Braun", "Annie Leonhart", "Jean Kirstein",
    "Sasha Blouse",

    "Izuku Midoriya", "Katsuki Bakugo", "Shoto Todoroki",
    "Ochaco Uraraka", "All Might", "Shota Aizawa",
    "Endeavor", "Hawks", "Tomura Shigaraki",
    "Eijiro Kirishima",

    "Ichigo Kurosaki", "Rukia Kuchiki", "Renji Abarai",
    "Byakuya Kuchiki", "Toshiro Hitsugaya",
    "Kenpachi Zaraki", "Kisuke Urahara", "Sosuke Aizen",
    "Yoruichi Shihouin", "Orihime Inoue",

    "Denji", "Power Chainsaw Man", "Makima Chainsaw Man",
    "Aki Hayakawa", "Pochita", "Kobeni Higashiyama",
    "Himeno Chainsaw Man", "Angel Devil Chainsaw Man",
    "Reze Chainsaw Man", "Katana Man Chainsaw Man",

    "Gon Freecss", "Killua Zoldyck", "Kurapika",
    "Leorio Paradinight", "Hisoka", "Chrollo Lucilfer",
    "Isaac Netero", "Biscuit Krueger", "Meruem",
    "Neferpitou",
]


def safe_filename(name):
    name = name.lower().replace(" ", "_")
    return re.sub(r"[^a-z0-9_]+", "", name)


def request_json(url):
    req = urllib.request.Request(
        url,
        headers={"User-Agent": "FigureverseImageDownloader/1.0"}
    )
    with urllib.request.urlopen(req, timeout=30) as response:
        return json.loads(response.read().decode("utf-8"))


def search_image(character):
    # Tìm ảnh theo tên nhân vật; ưu tiên kết quả có tên khớp.
    query = f'"{character}"'
    params = {
        "action": "query",
        "generator": "search",
        "gsrsearch": query,
        "gsrnamespace": 6,
        "gsrlimit": 10,
        "prop": "imageinfo",
        "iiprop": "url|extmetadata",
        "iiurlwidth": 700,
        "format": "json",
    }

    url = (
        "https://commons.wikimedia.org/w/api.php?"
        + urllib.parse.urlencode(params)
    )
    data = request_json(url)
    pages = data.get("query", {}).get("pages", {})

    candidates = []
    for page in pages.values():
        info_list = page.get("imageinfo", [])
        if not info_list:
            continue

        info = info_list[0]
        image_url = info.get("thumburl") or info.get("url", "")
        if not image_url:
            continue

        ext = Path(
            urllib.parse.urlparse(image_url).path
        ).suffix.lower()

        if ext not in {".jpg", ".jpeg", ".png", ".webp"}:
            continue

        metadata = info.get("extmetadata", {})
        candidates.append({
            "title": page.get("title", ""),
            "url": image_url,
            "source_page": info.get("descriptionurl", ""),
            "license": metadata.get(
                "LicenseShortName", {}
            ).get("value", "Chưa xác định"),
            "artist": metadata.get(
                "Artist", {}
            ).get("value", "Chưa xác định"),
        })

    return candidates


def download_file(url, destination):
    req = urllib.request.Request(
        url,
        headers={"User-Agent": "FigureverseImageDownloader/1.0"}
    )
    with urllib.request.urlopen(req, timeout=45) as response:
        data = response.read()

    # Bỏ qua tệp quá lớn hoặc phản hồi rỗng.
    if not data or len(data) > 12 * 1024 * 1024:
        raise ValueError("Kích thước ảnh không hợp lệ")

    destination.write_bytes(data)


def main():
    records = []

    for index, character in enumerate(CHARACTERS, start=1):
        print(f"[{index}/{len(CHARACTERS)}] {character}")

        filename = safe_filename(character) + ".jpg"
        destination = IMAGE_DIR / filename

        try:
            candidates = search_image(character)

            if not candidates:
                print("  Không tìm thấy ảnh.")
                records.append({
                    "character": character,
                    "status": "NOT_FOUND",
                    "file": "",
                    "candidates": [],
                })
                continue

            # Tải ứng viên đầu tiên để kiểm tra thủ công.
            # Kết quả tìm kiếm chưa đảm bảo đúng nhân vật.
            candidate = candidates[0]
            download_file(candidate["url"], destination)

            records.append({
                "character": character,
                "status": "REVIEW_REQUIRED",
                "file": filename,
                "candidates": candidates,
                "selected_source": candidate["source_page"],
                "license": candidate["license"],
                "artist": candidate["artist"],
            })

            print(f"  Đã tải ứng viên: {filename}")

        except Exception as exc:
            print(f"  Lỗi: {exc}")
            records.append({
                "character": character,
                "status": "ERROR",
                "file": "",
                "error": str(exc),
                "candidates": [],
            })

        # Tránh gửi yêu cầu quá nhanh đến dịch vụ tìm kiếm.
        time.sleep(1)

    report_path = BASE_DIR / "anime_image_sources.json"
    report_path.write_text(
        json.dumps(records, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )

    print("\nHoàn tất tìm kiếm.")
    print(f"Thư mục ảnh: {IMAGE_DIR}")
    print(f"Danh sách nguồn: {report_path}")
    print("Hãy kiểm tra ảnh và giấy phép trước khi sử dụng.")


if __name__ == "__main__":
    main()