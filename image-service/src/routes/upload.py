from fastapi import APIRouter, UploadFile, File, HTTPException
from fastapi.responses import FileResponse
from src.services.image_processing import ImageService
from src.config.config import settings
import os

router = APIRouter()

@router.post("/upload")
async def upload_image(file: UploadFile = File(...)):
    ext = ImageService.validate_image(file)
    paths = await ImageService.process_and_save(file, ext)
    return {"status": "success", "data": paths}

@router.get("/{year}/{month}/{day}/{filename}")
async def get_image(year: str, month: str, day: str, filename: str):
    file_path = os.path.join(settings.UPLOAD_DIR, year, month, day, filename)
    if not os.path.exists(file_path):
        raise HTTPException(status_code=404, detail="Image not found")
    return FileResponse(file_path)
