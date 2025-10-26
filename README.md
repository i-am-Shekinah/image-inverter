# Image Inversion API using Play Framework & Akka (Java 11)

## 📖 Overview

This project is a **Play Framework (Java 11)** application that demonstrates the use of **Akka concurrency and parallelism** to process images asynchronously.

The API allows users to:

* **Upload** an image.
* **Invert** the image colors using an Akka Actor system.
* **Retrieve** the inverted image later.

This is ideal for learning **Play + Akka integration**, **non-blocking IO**, and **concurrent processing** in Java web applications.

---

## 🧩 Tech Stack

* **Java 11**
* **Play Framework** (MVC architecture)
* **Akka Actors** for concurrent image processing
* **Swagger (OpenAPI)** for API documentation
* **Maven** for dependency management

---

## 🧠 How It Works

1. The user uploads an image via the `/upload` endpoint.
2. The image is saved in the `uploads/` directory.
3. The `ImageInverterService` sends the image file to an Akka actor (`ImageInverterActor`) for color inversion.
4. The actor processes the image concurrently — swapping color channels as follows:

    * Red → Green
    * Green → Blue
    * Blue → Red
5. The inverted image is saved in the `inverted/` directory.
6. The inverted image can then be retrieved via the `/image/:fileName` endpoint.

---

## 🚀 Endpoints

### 1. Upload and Invert Image

**POST** `/upload`

**Request Body:** `multipart/form-data`

```
Key: image
Value: <image file>
```

**Response:**

```
HTTP 200 OK
Image inverted and saved to: inverted/inverted_<filename>
```

### 2. Retrieve Inverted Image

**GET** `/image/:fileName`

**Response:** Returns the inverted image file.

Example:

```
GET /image/inverted_sample.png
```

---


```
app/
 ├── controllers/
 │    └── ImageController.java
 ├── services/
 │    └── ImageInverterService.java
 ├── actors/
 │    └── ImageInverterActor.java
 ├── uploads/              # Original uploaded images
 └── inverted/             # Inverted image output
```

---

## ⚙️ Build & Run

### 1. Clone the Repository

```bash
git clone https://github.com/i-am-Shekinah/image-inverter.git
cd image-inverter
```

### 2. Run the Application

```bash
mvn clean compile
mvn play2:run
```

The server will start on **[http://localhost:9000](http://localhost:9000)**.

---

## 📘 Swagger Documentation

Swagger UI provides an interactive interface for testing endpoints.

Once the app runs, visit:

```
http://localhost:9000/docs
```

---

## 🧪 Testing with cURL

### Upload Image

```bash
curl -X POST -F "image=@/path/to/your/image.png" http://localhost:9000/upload
```

### Retrieve Image

```bash
curl -O http://localhost:9000/image/inverted_image.png
```

---

## 🧠 Learning Highlights

* Asynchronous image processing using **Akka actors**.
* Non-blocking controller methods with `CompletionStage`.
* Integration of **Swagger** for Play-based REST APIs.
* File uploads & handling with Play’s `TemporaryFile` utilities.

---

## 📜 License

This project is for educational purposes and may be freely modified or extended.

---

## ✨ Author

**Michael Olatunji** - Software Engineer
