import os
from dotenv import load_dotenv

load_dotenv()

class Settings:
    PROJECT_NAME: str = "image-service"
    SERVER_PORT: int = int(os.getenv("SERVER_PORT", 8082))
    EUREKA_SERVER: str = os.getenv("EUREKA_SERVER", "http://localhost:8761/eureka")
    APP_NAME: str = os.getenv("APP_NAME", "image-service")
    INSTANCE_HOST: str = os.getenv("INSTANCE_HOST", "host.docker.internal") # Default for local docker
    UPLOAD_DIR: str = os.getenv("UPLOAD_DIR", "uploads")

settings = Settings()
