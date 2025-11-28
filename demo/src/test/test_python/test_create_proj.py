import requests

BASE_URL = "http://localhost:8081/api/projects"

def test_create_project_success():
    payload = {
        "name": "Proyecto desde pytest",
        "description": "Este es un proyecto creado desde pytest",
        "departmentId": 1,
        "startDate": "2025-12-01",
        "endDate": "2025-12-10",
        "budget": 10000,
        "priority": "HIGH",
        "status": "IN_PROGRESS",
        "sendEmail": False,
        "emailDestino": None
    }

    response = requests.post(BASE_URL, json=payload)

    print("STATUS:", response.status_code)
    print("BODY:", response.text)

    assert response.status_code == 201
