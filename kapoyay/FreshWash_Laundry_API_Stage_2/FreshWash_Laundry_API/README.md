# FreshWash Laundry API

A beginner-friendly REST API for a laundry shop, built with **Python, FastAPI, and SQLite**.

## Features
- Customer registration and login using bearer tokens
- Admin and customer roles
- Laundry service management
- Full CRUD for laundry orders (the primary resource)
- Payment records
- Automatic order total calculation from weight × service price
- Swagger/OpenAPI documentation
- Automated tests

## 1. Requirements
- Python 3.10 or newer
- Visual Studio Code (recommended)

## 2. Open the project
Extract the ZIP file, then open the `FreshWash_Laundry_API` folder in VS Code.

## 3. Create and activate a virtual environment

**Windows PowerShell**
```powershell
py -m venv .venv
.\.venv\Scripts\Activate.ps1
```

**Windows Command Prompt**
```bat
py -m venv .venv
.venv\Scripts\activate
```

**macOS / Linux**
```bash
python3 -m venv .venv
source .venv/bin/activate
```

## 4. Install dependencies
```bash
python -m pip install -r requirements.txt
```

## 5. Configure the application
Copy `.env.example` to `.env`. For local classroom testing, the default settings work. Change `SECRET_KEY` before deploying the API publicly.

The app reads `DATABASE_URL` and `SECRET_KEY` from environment variables. If they are not set, local defaults are used.

## 6. Create the admin account
Run:
```bash
python create_admin.py
```
Follow the prompts to enter the admin name, email, and password. The password is stored as a secure PBKDF2 hash, not plain text.

## 7. Run the API
```bash
uvicorn app.main:app --reload
```
Open:
- Swagger UI: http://127.0.0.1:8000/docs
- ReDoc: http://127.0.0.1:8000/redoc
- Health check: http://127.0.0.1:8000/health

The SQLite database file `freshwash.db` is created automatically in the project folder.

## 8. Login and authorize in Swagger
1. Use `POST /api/v1/auth/login` with the account email and password.
2. Copy the returned `access_token`.
3. Click **Authorize** in Swagger UI and enter `Bearer YOUR_TOKEN`.
4. Try the protected endpoints based on the account role.

New registrations always receive the `customer` role. Only an administrator created with `create_admin.py` receives the `admin` role.

## 9. Run the tests
In the activated virtual environment:
```bash
pytest -v
```

## Main API endpoints

| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/v1/auth/register` | Public |
| POST | `/api/v1/auth/login` | Public |
| GET | `/api/v1/users` | Admin |
| GET | `/api/v1/users/me` | Authenticated |
| GET | `/api/v1/users/{user_id}` | Admin or account owner |
| PUT | `/api/v1/users/{user_id}` | Admin or account owner |
| DELETE | `/api/v1/users/{user_id}` | Admin |
| POST | `/api/v1/services` | Admin |
| GET | `/api/v1/services` | Public |
| GET | `/api/v1/services/{service_id}` | Public |
| PUT | `/api/v1/services/{service_id}` | Admin |
| DELETE | `/api/v1/services/{service_id}` | Admin |
| POST | `/api/v1/orders` | Customer |
| GET | `/api/v1/orders` | Admin sees all; customer sees own |
| GET | `/api/v1/orders/{order_id}` | Admin or owner |
| PUT | `/api/v1/orders/{order_id}` | Admin or owner (limited fields) |
| DELETE | `/api/v1/orders/{order_id}` | Admin |
| POST | `/api/v1/payments` | Customer |
| GET | `/api/v1/payments` | Admin sees all; customer sees own |
| GET | `/api/v1/payments/{payment_id}` | Admin or owner |
| PUT | `/api/v1/payments/{payment_id}` | Admin |

## Example order request
```json
{
  "service_id": 1,
  "weight_kg": 4.5
}
```

The API calculates `total_price` using the service's current `price_per_kg`. Customers cannot set another user's ID, order status, or total price. Admins can update order status.

## Important notes
- This is a classroom project and uses a local SQLite database.
- Payment records are simulated records only. The API does not process real card or online payments.
- For production use, add HTTPS, stronger secret management, migrations, rate limiting, and a real payment provider.
