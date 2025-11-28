import requests

BASE_URL = "http://localhost:8081/api/auth/login"

def test_find_by_username_or_email():
    payload = {
        "usernameOrEmail": "Juannomesque12@gmail.com",
        "password": "juanes.12"      # <-- usa un password REAL de tu BD
    }

    r = requests.post(BASE_URL, json=payload)

    # 1. Verificar que el endpoint funciona
    assert r.status_code == 200, f"Login failed: {r.text}"

    data = r.json()

    # 2. Como userService encontró al usuario, la respuesta tiene:
    # token, name, username, email
    assert "email" in data
    assert data["email"] == payload["usernameOrEmail"]

    assert "token" in data, "Token was not returned"

