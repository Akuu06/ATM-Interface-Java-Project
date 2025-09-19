# 🏧 ATM Interface System  

A **Java Swing-based ATM Interface System** that provides secure banking operations with **MySQL database integration**.  
This application simulates real ATM functionalities including balance inquiry, cash withdrawal, deposits, and PIN changes with robust authentication and data persistence.  

---

## 🚀 Features  

| ✅ Feature               | 📄 Description |
|------------------------|---------------|
| **Secure Login System** | Username and PIN authentication with 3-attempt limit |
| **Core ATM Operations** | Withdraw, deposit, and check balance |
| **PIN Management** | Secure PIN change functionality with validation |
| **Database Integration** | Persistent storage using MySQL |
| **Input Validation** | Ensures valid transaction amounts and PIN requirements |
| **User-Friendly Interface** | Clean, intuitive GUI with real-time balance display |
| **Transaction History** | Displays success/failure messages for all operations |
| **Initial Deposit Setup** | Prompts for initial deposit on first use |

---

## 🖼️ Demo  

https://lnkd.in/p/d9fufwMM

---

## 🛠️ Tech Stack  

- ☕ **Java (Swing GUI)** – Frontend interface and application logic  
- 🗄️ **MySQL** – Database for persistent user data storage  
- 🔌 **JDBC** – Database connectivity  
- 🎨 **Nimbus Look and Feel** – Modern UI theme  

---

## ⚙️ Installation & Usage  

### 📥 Prerequisites  

- **Java Development Kit (JDK) 8 or higher**  
- **MySQL Server 5.7 or higher**  
- **MySQL Connector/J driver** (download and add to classpath)  

---

### 🗄️ Database Setup  

1. **Create a MySQL database named `atm_db`:**  
   ```sql
   CREATE DATABASE atm_db;

2. ### 🗄️ Update MySQL Credentials  

Update MySQL credentials in **`DatabaseHelper.java`**:  

```java
private static final String DB_USER = "your_username";
private static final String DB_PASSWORD = "your_password";

```
### ▶️ Running the Application  

#### 1️⃣ Clone the repository  
```bash
git clone https://github.com/Akuu06/ATM-Interface.git
cd ATM-Interface

```
#### 2️⃣ Compile the Java files with MySQL connector  
```bash
javac -cp ".;mysql-connector-java-8.0.28.jar" *.java

```
#### 3️⃣ Run the application  
```bash
java -cp ".;mysql-connector-java-8.0.28.jar" ATM

```
### 🔑 Default Login Credentials  

| Field       | Value   |
|------------|--------|
| **Username** | `user` |
| **PIN**      | `1234` |

## 👨‍💻 Author  
**Akanksha Kamra**  

🌐 **Connect with me:**  
- 🔗 [LinkedIn](https://www.linkedin.com/in/akanksha-kamra)  
- 💻 [GitHub](https://github.com/Akuu06)  


