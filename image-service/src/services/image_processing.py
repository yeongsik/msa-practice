import os
import uuid
from datetime import datetime
from PIL import Image
from fastapi import UploadFile, HTTPException
from src.config.config import settings

ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "webp"}
MAX_FILE_SIZE = 5 * 1024 * 1024  # 5MB

class ImageService:
    @staticmethod
    def validate_image(file: UploadFile):
        # 1. Check file extension
        filename = file.filename.lower() if file.filename else ""
        ext = filename.split(".")[-1] if "." in filename else ""
        if ext not in ALLOWED_EXTENSIONS:
            raise HTTPException(status_code=400, detail="Invalid file type. Only JPG, PNG, WEBP allowed.")

        # 2. Check content type (MIME)
        if file.content_type not in ["image/jpeg", "image/png", "image/webp"]:
             raise HTTPException(status_code=400, detail="Invalid MIME type.")

        return ext

    @staticmethod
    async def process_and_save(file: UploadFile, ext: str):
        # Create directory structure: uploads/YYYY/MM/DD
        now = datetime.now()
        relative_path = f"{now.year}/{now.month:02d}/{now.day:02d}"
        upload_path = os.path.join(settings.UPLOAD_DIR, relative_path)
        os.makedirs(upload_path, exist_ok=True)

        # Generate unique ID
        file_id = str(uuid.uuid4())
        base_filename = f"{file_id}"
        
        # Read image content
        content = await file.read()
        if len(content) > MAX_FILE_SIZE:
             raise HTTPException(status_code=400, detail="File too large. Max 5MB.")

        # Save Original
        original_filename = f"{base_filename}_original.{ext}"
        original_path = os.path.join(upload_path, original_filename)
        
        with open(original_path, "wb") as f:
            f.write(content)
            
        # Process Images (Thumbnail & Profile) using Pillow
        # Reset cursor
        await file.seek(0) 
        
        try:
            with Image.open(file.file) as img:
                # 1. Generate Profile (800x800 max)
                profile_img = img.copy()
                profile_img.thumbnail((800, 800))
                profile_filename = f"{base_filename}_profile.{ext}"
                profile_path = os.path.join(upload_path, profile_filename)
                profile_img.save(profile_path, optimize=True, quality=85)

                # 2. Generate Thumbnail (200x200 max)
                thumb_img = img.copy()
                thumb_img.thumbnail((200, 200))
                thumb_filename = f"{base_filename}_thumbnail.{ext}"
                thumb_path = os.path.join(upload_path, thumb_filename)
                thumb_img.save(thumb_path, optimize=True, quality=75)
                
                return {
                    "original": f"/{relative_path}/{original_filename}",
                    "profile": f"/{relative_path}/{profile_filename}",
                    "thumbnail": f"/{relative_path}/{thumb_filename}"
                }
        except Exception as e:
            # Cleanup if failed
            if os.path.exists(original_path): os.remove(original_path)
            raise HTTPException(status_code=500, detail=f"Image processing failed: {str(e)}")
