# File-status-processor
## Description:
File-status-processor is needed to check update and create the status of a file in the database. The service monitors notifications about file status and updates it.

---

**Before you start the service you need to make sure that all data servers are up and running.**

---

**The docker-compose.yml file for running data servers is located in the `file-uploader` service. It must be launched before all services are started.**

---


**The application port is 8444**

---

# API Endpoint
**This endpoint allows you to check the current status of a file by providing its unique checksum. All requests must be made using the HTTPS protocol.**

- Method: **GET**

- URL: `https://localhost:8444/status`

* **Query Parameters**:
   * `checksum` (string): The unique checksum of the file.

**Response**: The endpoint returns a FileDto object containing the file's details and status.

