import pytest
import requests

BASE_URL = "http://localhost:8081/api/departments"

def test_create_department_real():
    request_body = {
        "name": "Finance",
        "description": "Financial operations"
    }

    response = requests.post(BASE_URL, json=request_body)

    assert response.status_code == 201
    response_json = response.json()

    assert response_json["name"] == "Finance"
    assert response_json["description"] == "Financial operations"


