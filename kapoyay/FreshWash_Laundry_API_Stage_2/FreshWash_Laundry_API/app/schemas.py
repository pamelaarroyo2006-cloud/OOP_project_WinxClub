from datetime import datetime
from typing import Literal
from pydantic import BaseModel, ConfigDict, EmailStr, Field

class UserRegister(BaseModel):
    full_name: str = Field(min_length=2, max_length=120)
    email: EmailStr
    password: str = Field(min_length=8, max_length=128)

class UserOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    id: int
    full_name: str
    email: EmailStr
    role: str
    created_at: datetime

class TokenOut(BaseModel):
    access_token: str
    token_type: str = "bearer"

class ServiceCreate(BaseModel):
    service_name: str = Field(min_length=2, max_length=100)
    description: str = Field(default="", max_length=500)
    price_per_kg: float = Field(gt=0)
    is_available: bool = True

class ServiceUpdate(BaseModel):
    service_name: str | None = Field(default=None, min_length=2, max_length=100)
    description: str | None = Field(default=None, max_length=500)
    price_per_kg: float | None = Field(default=None, gt=0)
    is_available: bool | None = None

class ServiceOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    id: int
    service_name: str
    description: str
    price_per_kg: float
    is_available: bool

class OrderCreate(BaseModel):
    service_id: int
    weight_kg: float = Field(gt=0, le=1000)

class OrderUpdate(BaseModel):
    weight_kg: float | None = Field(default=None, gt=0, le=1000)
    status: Literal["Pending", "Processing", "Ready for Pickup", "Completed", "Cancelled"] | None = None

class OrderOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    id: int
    user_id: int
    service_id: int
    weight_kg: float
    price_per_kg: float
    total_price: float
    status: str
    payment_status: str
    created_at: datetime
    updated_at: datetime

class PaymentCreate(BaseModel):
    order_id: int
    amount: float = Field(gt=0)
    payment_method: Literal["Cash", "GCash", "Bank Transfer"]

class PaymentUpdate(BaseModel):
    payment_status: Literal["Paid", "Unpaid", "Refunded"]

class PaymentOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    id: int
    order_id: int
    amount: float
    payment_method: str
    payment_status: str
    payment_date: datetime
