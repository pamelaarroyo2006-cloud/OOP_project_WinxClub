import os

DATABASE_URL = os.getenv("DATABASE_URL", "sqlite:///./freshwash.db")
SECRET_KEY = os.getenv("SECRET_KEY", "local-development-secret-change-before-deployment")
ACCESS_TOKEN_EXPIRE_MINUTES = int(os.getenv("ACCESS_TOKEN_EXPIRE_MINUTES", "60"))
ALGORITHM = "HS256"
