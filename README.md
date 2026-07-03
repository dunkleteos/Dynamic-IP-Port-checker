# Dynamic IP Port Checker

Dynamic IP Port Checker is a Java-based network monitoring tool designed to track server status via dynamic socket connections. It reads server IPs and ports from an external file and performs real-time uptime audits. Features a GMP-compliant retry logic to minimize false alarms and distinct exception handling for high fault tolerance.

---

## 🛠️ How to Setup and Run

Follow these simple steps to configure and run the monitoring system:

### Step 1: Create the Configuration File
In the root directory of your project (where the code runs), create a plain text file named **`servers.txt`**.

### Step 2: Populate the File
Add your target servers to `servers.txt` using the format: **Server_Name IP_Address:Port Risk_Level**. Use exactly **one space** between values.

**Example `servers.txt` content:**
```text
Google_DNS_Server 8.8.8.8:53 HIGH
Cloudflare_DNS_Server 1.1.1.1:53 HIGH
Local_Test_Server 127.0.0.1:9999 LOW
