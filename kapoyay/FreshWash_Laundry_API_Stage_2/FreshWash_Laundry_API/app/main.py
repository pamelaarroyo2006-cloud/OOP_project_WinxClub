from contextlib import asynccontextmanager
from fastapi import Depends, FastAPI, HTTPException, status
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session

from app.database import Base, engine, get_db
from app.models import User, Service, Order, Payment
from app.schemas import (
    UserRegister, UserOut, TokenOut, ServiceCreate, ServiceUpdate, ServiceOut,
    OrderCreate, OrderUpdate, OrderOut, PaymentCreate, PaymentUpdate, PaymentOut
)
from app.security import hash_password, verify_password, create_access_token
from app.dependencies import get_current_user, require_admin

@asynccontextmanager
async def lifespan(app: FastAPI):
    Base.metadata.create_all(bind=engine)
    yield

app = FastAPI(
    title="FreshWash Laundry API",
    description="A RESTful API for managing laundry customers, services, orders, and payments.",
    version="1.0.0",
    lifespan=lifespan,
)

@app.get("/health", tags=["Health"])
def health_check():
    return {"status": "healthy", "project": "FreshWash Laundry API"}

@app.post("/api/v1/auth/register", response_model=UserOut, status_code=status.HTTP_201_CREATED, tags=["Authentication"])
def register(data: UserRegister, db: Session = Depends(get_db)):
    email = data.email.lower()
    if db.query(User).filter(User.email == email).first():
        raise HTTPException(status_code=409, detail="Email is already registered")
    user = User(full_name=data.full_name, email=email, password_hash=hash_password(data.password), role="customer")
    db.add(user)
    db.commit()
    db.refresh(user)
    return user

@app.post("/api/v1/auth/login", response_model=TokenOut, tags=["Authentication"])
def login(form: OAuth2PasswordRequestForm = Depends(), db: Session = Depends(get_db)):
    user = db.query(User).filter(User.email == form.username.lower()).first()
    if user is None or not verify_password(form.password, user.password_hash):
        raise HTTPException(status_code=401, detail="Incorrect email or password")
    return {"access_token": create_access_token(user.id, user.role), "token_type": "bearer"}

@app.get("/api/v1/users/me", response_model=UserOut, tags=["Users"])
def read_my_profile(current_user: User = Depends(get_current_user)):
    return current_user

@app.get("/api/v1/users", response_model=list[UserOut], tags=["Users"])
def list_users(db: Session = Depends(get_db), _: User = Depends(require_admin)):
    return db.query(User).order_by(User.id).all()

@app.get("/api/v1/users/{user_id}", response_model=UserOut, tags=["Users"])
def get_user(user_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    user = db.query(User).filter(User.id == user_id).first()
    if user is None:
        raise HTTPException(status_code=404, detail="User not found")
    if current_user.role != "admin" and current_user.id != user_id:
        raise HTTPException(status_code=403, detail="You can only view your own account")
    return user

@app.put("/api/v1/users/{user_id}", response_model=UserOut, tags=["Users"])
def update_user(user_id: int, data: UserRegister, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    user = db.query(User).filter(User.id == user_id).first()
    if user is None:
        raise HTTPException(status_code=404, detail="User not found")
    if current_user.role != "admin" and current_user.id != user_id:
        raise HTTPException(status_code=403, detail="You can only update your own account")
    user.full_name = data.full_name
    user.email = data.email.lower()
    user.password_hash = hash_password(data.password)
    try:
        db.commit()
        db.refresh(user)
    except IntegrityError:
        db.rollback()
        raise HTTPException(status_code=409, detail="Email is already in use")
    return user

@app.delete("/api/v1/users/{user_id}", status_code=status.HTTP_204_NO_CONTENT, tags=["Users"])
def delete_user(user_id: int, db: Session = Depends(get_db), _: User = Depends(require_admin)):
    user = db.query(User).filter(User.id == user_id).first()
    if user is None:
        raise HTTPException(status_code=404, detail="User not found")
    if user.orders:
        raise HTTPException(status_code=409, detail="Cannot delete a user who has existing orders")
    db.delete(user)
    db.commit()
    return None

@app.post("/api/v1/services", response_model=ServiceOut, status_code=201, tags=["Services"])
def create_service(data: ServiceCreate, db: Session = Depends(get_db), _: User = Depends(require_admin)):
    service = Service(**data.model_dump())
    db.add(service)
    try:
        db.commit()
        db.refresh(service)
    except IntegrityError:
        db.rollback()
        raise HTTPException(status_code=409, detail="Service name already exists")
    return service

@app.get("/api/v1/services", response_model=list[ServiceOut], tags=["Services"])
def list_services(db: Session = Depends(get_db)):
    return db.query(Service).order_by(Service.id).all()

@app.get("/api/v1/services/{service_id}", response_model=ServiceOut, tags=["Services"])
def get_service(service_id: int, db: Session = Depends(get_db)):
    service = db.query(Service).filter(Service.id == service_id).first()
    if service is None:
        raise HTTPException(status_code=404, detail="Service not found")
    return service

@app.put("/api/v1/services/{service_id}", response_model=ServiceOut, tags=["Services"])
def update_service(service_id: int, data: ServiceUpdate, db: Session = Depends(get_db), _: User = Depends(require_admin)):
    service = db.query(Service).filter(Service.id == service_id).first()
    if service is None:
        raise HTTPException(status_code=404, detail="Service not found")
    for key, value in data.model_dump(exclude_unset=True).items():
        setattr(service, key, value)
    try:
        db.commit()
        db.refresh(service)
    except IntegrityError:
        db.rollback()
        raise HTTPException(status_code=409, detail="Service name already exists")
    return service

@app.delete("/api/v1/services/{service_id}", status_code=204, tags=["Services"])
def delete_service(service_id: int, db: Session = Depends(get_db), _: User = Depends(require_admin)):
    service = db.query(Service).filter(Service.id == service_id).first()
    if service is None:
        raise HTTPException(status_code=404, detail="Service not found")
    if db.query(Order).filter(Order.service_id == service_id).first():
        raise HTTPException(status_code=409, detail="Cannot delete a service used by an order")
    db.delete(service)
    db.commit()
    return None

@app.post("/api/v1/orders", response_model=OrderOut, status_code=201, tags=["Orders"])
def create_order(data: OrderCreate, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    service = db.query(Service).filter(Service.id == data.service_id).first()
    if service is None or not service.is_available:
        raise HTTPException(status_code=404, detail="Available laundry service not found")
    total = round(data.weight_kg * service.price_per_kg, 2)
    order = Order(user_id=current_user.id, service_id=service.id, weight_kg=data.weight_kg,
                  price_per_kg=service.price_per_kg, total_price=total)
    db.add(order)
    db.commit()
    db.refresh(order)
    return order

@app.get("/api/v1/orders", response_model=list[OrderOut], tags=["Orders"])
def list_orders(db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    query = db.query(Order)
    if current_user.role != "admin":
        query = query.filter(Order.user_id == current_user.id)
    return query.order_by(Order.id.desc()).all()

@app.get("/api/v1/orders/{order_id}", response_model=OrderOut, tags=["Orders"])
def get_order(order_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    order = db.query(Order).filter(Order.id == order_id).first()
    if order is None:
        raise HTTPException(status_code=404, detail="Order not found")
    if current_user.role != "admin" and order.user_id != current_user.id:
        raise HTTPException(status_code=403, detail="You cannot access another customer's order")
    return order

@app.put("/api/v1/orders/{order_id}", response_model=OrderOut, tags=["Orders"])
def update_order(order_id: int, data: OrderUpdate, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    order = db.query(Order).filter(Order.id == order_id).first()
    if order is None:
        raise HTTPException(status_code=404, detail="Order not found")
    changes = data.model_dump(exclude_unset=True, exclude_none=True)
    if current_user.role != "admin":
        if order.user_id != current_user.id:
            raise HTTPException(status_code=403, detail="You cannot update another customer's order")
        if "status" in changes:
            raise HTTPException(status_code=403, detail="Only an administrator can change order status")
        if order.status != "Pending":
            raise HTTPException(status_code=409, detail="Only pending orders can be edited by customers")
    if "weight_kg" in changes:
        order.weight_kg = changes["weight_kg"]
        order.total_price = round(order.weight_kg * order.price_per_kg, 2)
    if "status" in changes and current_user.role == "admin":
        order.status = changes["status"]
    db.commit()
    db.refresh(order)
    return order

@app.delete("/api/v1/orders/{order_id}", status_code=204, tags=["Orders"])
def delete_order(order_id: int, db: Session = Depends(get_db), _: User = Depends(require_admin)):
    order = db.query(Order).filter(Order.id == order_id).first()
    if order is None:
        raise HTTPException(status_code=404, detail="Order not found")
    if db.query(Payment).filter(Payment.order_id == order_id).first():
        raise HTTPException(status_code=409, detail="Cannot delete an order with payment records")
    db.delete(order)
    db.commit()
    return None

@app.post("/api/v1/payments", response_model=PaymentOut, status_code=201, tags=["Payments"])
def create_payment(data: PaymentCreate, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    order = db.query(Order).filter(Order.id == data.order_id).first()
    if order is None:
        raise HTTPException(status_code=404, detail="Order not found")
    if current_user.role != "admin" and order.user_id != current_user.id:
        raise HTTPException(status_code=403, detail="You cannot pay for another customer's order")
    if data.amount != order.total_price:
        raise HTTPException(status_code=400, detail=f"Payment amount must equal the order total ({order.total_price:.2f})")
    if order.payment_status == "Paid":
        raise HTTPException(status_code=409, detail="Order has already been paid")
    payment = Payment(order_id=order.id, amount=data.amount, payment_method=data.payment_method, payment_status="Paid")
    order.payment_status = "Paid"
    db.add(payment)
    db.commit()
    db.refresh(payment)
    return payment

@app.get("/api/v1/payments", response_model=list[PaymentOut], tags=["Payments"])
def list_payments(db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    query = db.query(Payment).join(Order)
    if current_user.role != "admin":
        query = query.filter(Order.user_id == current_user.id)
    return query.order_by(Payment.id.desc()).all()

@app.get("/api/v1/payments/{payment_id}", response_model=PaymentOut, tags=["Payments"])
def get_payment(payment_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    payment = db.query(Payment).filter(Payment.id == payment_id).first()
    if payment is None:
        raise HTTPException(status_code=404, detail="Payment not found")
    if current_user.role != "admin" and payment.order.user_id != current_user.id:
        raise HTTPException(status_code=403, detail="You cannot access another customer's payment")
    return payment

@app.put("/api/v1/payments/{payment_id}", response_model=PaymentOut, tags=["Payments"])
def update_payment(payment_id: int, data: PaymentUpdate, db: Session = Depends(get_db), _: User = Depends(require_admin)):
    payment = db.query(Payment).filter(Payment.id == payment_id).first()
    if payment is None:
        raise HTTPException(status_code=404, detail="Payment not found")
    payment.payment_status = data.payment_status
    payment.order.payment_status = data.payment_status if data.payment_status in ("Paid", "Unpaid") else "Unpaid"
    db.commit()
    db.refresh(payment)
    return payment
