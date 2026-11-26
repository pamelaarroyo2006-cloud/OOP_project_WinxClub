import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.pool import StaticPool

from app.database import Base, get_db
from app.main import app
from app.models import User, Service
from app.security import hash_password

@pytest.fixture()
def client():
    test_engine = create_engine(
        "sqlite://",
        connect_args={"check_same_thread": False},
        poolclass=StaticPool,
    )
    TestingSessionLocal = sessionmaker(bind=test_engine, autoflush=False, autocommit=False)
    Base.metadata.create_all(bind=test_engine)

    def override_get_db():
        db = TestingSessionLocal()
        try:
            yield db
        finally:
            db.close()

    app.dependency_overrides[get_db] = override_get_db
    db = TestingSessionLocal()
    db.add(User(full_name="Test Admin", email="admin@example.com", password_hash=hash_password("adminpass123"), role="admin"))
    db.add(Service(service_name="Wash & Fold", description="Standard wash and fold", price_per_kg=50.0, is_available=True))
    db.commit()
    db.close()

    with TestClient(app) as test_client:
        yield test_client
    app.dependency_overrides.clear()
    Base.metadata.drop_all(bind=test_engine)
    test_engine.dispose()

@pytest.fixture()
def customer_token(client):
    response = client.post("/api/v1/auth/register", json={
        "full_name": "Test Customer",
        "email": "customer@example.com",
        "password": "customerpass123"
    })
    assert response.status_code == 201
    login = client.post("/api/v1/auth/login", data={
        "username": "customer@example.com",
        "password": "customerpass123"
    })
    assert login.status_code == 200
    return login.json()["access_token"]

@pytest.fixture()
def admin_token(client):
    login = client.post("/api/v1/auth/login", data={
        "username": "admin@example.com",
        "password": "adminpass123"
    })
    assert login.status_code == 200
    return login.json()["access_token"]
