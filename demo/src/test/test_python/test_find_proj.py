import requests

BASE_URL = "http://localhost:8081/api/projects"   # Cambia si tu URL difiere


def test_get_project_by_id_success():
    """
    Prueba que al consultar un proyecto existente por ID,
    el servidor devuelve 200 OK y el objeto del proyecto.
    """

    project_id = 1  # Usa un ID REAL que sí exista en tu base de datos

    response = requests.get(f"{BASE_URL}/{project_id}")

    print("STATUS:", response.status_code)
    print("BODY:", response.text)

    assert response.status_code == 200
    data = response.json()
    assert data["id"] == project_id
