import requests
import os
import io
import sys

# Try to import PIL for image generation, otherwise use a fallback
try:
    from PIL import Image
    HAS_PIL = True
except ImportError:
    HAS_PIL = False

BASE_URL = os.getenv("IMAGE_SERVICE_URL", "http://localhost:8082")

def create_test_image():
    if HAS_PIL:
        print("Generating test image using Pillow...")
        img = Image.new('RGB', (600, 400), color=(73, 109, 137))
        img_byte_arr = io.BytesIO()
        img.save(img_byte_arr, format='PNG')
        return img_byte_arr.getvalue()
    else:
        # Fallback: A tiny valid 1x1 PNG pixel (base64)
        print("Pillow not found, using fallback 1x1 PNG...")
        return b'\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR\x00\x00\x00\x01\x00\x00\x00\x01\x08\x02\x00\x00\x00\x90wS\xde\x00\x00\x00\x0cIDATx\x9cc\xf8\xff\xff?\x00\x05\xfe\x02\xfe\xdcD\xee\xce\x00\x00\x00\x00IEND\xaeB`\x82'

def test_health():
    print(f"Checking health at {BASE_URL}/health...")
    try:
        response = requests.get(f"{BASE_URL}/health")
        response.raise_for_status()
        print(f"Health Check: {response.json()}")
        return True
    except Exception as e:
        print(f"Health Check Failed: {e}")
        return False

def test_upload():
    print("\nTesting Image Upload...")
    image_data = create_test_image()
    files = {'file': ('test_image.png', image_data, 'image/png')}
    
    try:
        response = requests.post(f"{BASE_URL}/api/images/upload", files=files)
        if response.status_code == 200:
            result = response.json()
            print("Upload Successful!")
            print(f"Generated Paths: {result['data']}")
            return result['data']
        else:
            print(f"Upload Failed (Status {response.status_code}): {response.text}")
            return None
    except Exception as e:
        print(f"Error during upload: {e}")
        return None

def test_download(paths):
    print("\nTesting Image Downloads...")
    for version, path in paths.items():
        url = f"{BASE_URL}/api/images{path}"
        try:
            response = requests.get(url)
            if response.status_code == 200:
                print(f"OK: Downloaded {version} ({len(response.content)} bytes) from {url}")
            else:
                print(f"FAIL: Could not download {version} from {url} (Status {response.status_code})")
        except Exception as e:
            print(f"Error downloading {version}: {e}")

if __name__ == "__main__":
    if not test_health():
        print("\nERROR: Service is not reachable. Make sure 'docker-compose up' is running and port 8082 is open.")
        sys.exit(1)
        
    paths = test_upload()
    if paths:
        test_download(paths)
        print("\nAll tests completed!")
    else:
        print("\nTests failed at upload stage.")
        sys.exit(1)
