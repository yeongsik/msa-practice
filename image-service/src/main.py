import sys
import os
import asyncio
from contextlib import asynccontextmanager
from fastapi import FastAPI
import py_eureka_client.eureka_client as eureka_client
from src.config.config import settings
from src.routes import upload

# Ensure upload directory exists
os.makedirs(settings.UPLOAD_DIR, exist_ok=True)

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup: Register with Eureka
    print(f"Starting {settings.APP_NAME}...")
    await eureka_client.init_async(
        eureka_server=settings.EUREKA_SERVER,
        app_name=settings.APP_NAME,
        instance_port=settings.SERVER_PORT,
        instance_host=settings.INSTANCE_HOST
    )
    yield
    # Shutdown: De-register is handled automatically by py_eureka_client but good to know
    print(f"Stopping {settings.APP_NAME}...")
    await eureka_client.stop_async()

app = FastAPI(title=settings.PROJECT_NAME, lifespan=lifespan)

app.include_router(upload.router, prefix="/api/images", tags=["images"])

@app.get("/health")
async def health_check():
    return {"status": "UP", "service": settings.APP_NAME}

@app.get("/")
async def root():
    return {"message": f"Welcome to {settings.APP_NAME}"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("src.main:app", host="0.0.0.0", port=settings.SERVER_PORT, reload=True)
