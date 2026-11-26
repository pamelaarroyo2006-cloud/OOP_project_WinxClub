def auth(token):
    return {"Authorization": f"Bearer {token}"}

def test_register_customer(client):
    response = client.post("/api/v1/auth/register", json={
        "full_name": "Maria Santos",
        "email": "maria@example.com",
        "password": "password123"
    })
    assert response.status_code == 201
    assert response.json()["role"] == "customer"
    assert "password_hash" not in response.json()

def test_login_returns_bearer_token(client, customer_token):
    response = client.get("/api/v1/users/me", headers=auth(customer_token))
    assert response.status_code == 200
    assert response.json()["email"] == "customer@example.com"

def test_customer_can_create_and_read_order(client, customer_token):
    created = client.post("/api/v1/orders", json={"service_id": 1, "weight_kg": 3.5}, headers=auth(customer_token))
    assert created.status_code == 201
    body = created.json()
    assert body["total_price"] == 175.0
    listed = client.get("/api/v1/orders", headers=auth(customer_token))
    assert listed.status_code == 200
    assert len(listed.json()) == 1
    assert listed.json()[0]["id"] == body["id"]

def test_customer_cannot_change_order_status(client, customer_token):
    created = client.post("/api/v1/orders", json={"service_id": 1, "weight_kg": 2}, headers=auth(customer_token))
    order_id = created.json()["id"]
    response = client.put(f"/api/v1/orders/{order_id}", json={"status": "Completed"}, headers=auth(customer_token))
    assert response.status_code == 403

def test_admin_can_update_order_status(client, customer_token, admin_token):
    created = client.post("/api/v1/orders", json={"service_id": 1, "weight_kg": 2}, headers=auth(customer_token))
    order_id = created.json()["id"]
    response = client.put(f"/api/v1/orders/{order_id}", json={"status": "Processing"}, headers=auth(admin_token))
    assert response.status_code == 200
    assert response.json()["status"] == "Processing"

def test_customer_can_make_payment(client, customer_token):
    created = client.post("/api/v1/orders", json={"service_id": 1, "weight_kg": 2}, headers=auth(customer_token))
    order_id = created.json()["id"]
    payment = client.post("/api/v1/payments", json={
        "order_id": order_id,
        "amount": 100.0,
        "payment_method": "Cash"
    }, headers=auth(customer_token))
    assert payment.status_code == 201
    assert payment.json()["payment_status"] == "Paid"

def test_non_admin_cannot_create_service(client, customer_token):
    response = client.post("/api/v1/services", json={
        "service_name": "Express Wash",
        "description": "Fast service",
        "price_per_kg": 75
    }, headers=auth(customer_token))
    assert response.status_code == 403

def test_order_not_found_returns_404(client, customer_token):
    response = client.get("/api/v1/orders/999", headers=auth(customer_token))
    assert response.status_code == 404
