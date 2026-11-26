from getpass import getpass

from app.database import Base, engine, SessionLocal
from app.models import User
from app.security import hash_password

def main():
    Base.metadata.create_all(bind=engine)
    name = input("Admin full name: ").strip()
    email = input("Admin email: ").strip().lower()
    password = getpass("Admin password (minimum 8 characters): ")
    if not name or not email or len(password) < 8:
        print("Name and email are required; password must be at least 8 characters.")
        return
    db = SessionLocal()
    try:
        if db.query(User).filter(User.email == email).first():
            print("An account with that email already exists.")
            return
        admin = User(full_name=name, email=email, password_hash=hash_password(password), role="admin")
        db.add(admin)
        db.commit()
        print(f"Admin account created for {email}.")
    finally:
        db.close()

if __name__ == "__main__":
    main()
