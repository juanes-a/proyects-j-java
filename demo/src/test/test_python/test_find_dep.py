import requests
import pytest

BASE_URL = "http://localhost:8081/api/departments"

def test_get_department_by_id_success():
    department_id = 1
    response = requests.get(f"{BASE_URL}/{department_id}")

    assert response.status_code == 200

    data = response.json()
    assert data["id"] == department_id
    assert "name" in data
    assert "description" in data


def test_get_department_by_id_not_found():
    department_id = 9999
    response = requests.get(f"{BASE_URL}/{department_id}")

    assert response.status_code == 404

    # Backend puede devolver JSON o cuerpo vacío
    if response.text.strip():
        assert "Departamento no encontrado" in response.text
    else:
        # Si no hay cuerpo, se acepta como comportamiento válido
        assert True


