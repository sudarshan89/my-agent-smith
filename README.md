# 🕶️ My Agent Smith

A hands-on exercise to explore **Temporal** as a durable workflow engine and integrate it with a simple **Java application**. You’ll spin up Temporal in a development environment and connect it to your own Java-based worker using OpenJDK 17.

This setup is intended for learning and experimentation purposes in a Cloud9 environment or similar AWS-hosted dev workspace.

### 🧠 What does this app do?

This app simulates an autonomous agent that monitors a target web page on a recurring schedule. The workflow:

* Periodically polls the web page.
* Summarises the difference from its previous state using a foundation model via **Amazon Bedrock**.
* Produces a developer-friendly summary of meaningful changes.
* Reasons about the most suitable communication channels (e.g., Slack, email, issue tracker) on which the summary should be posted.
* Automatically selects and posts the summary to those channels.
* *Optional extension*: The agent can be enhanced to monitor engagement on those channels (e.g., reactions, replies, click-throughs) and adapt future posting strategies based on what works best.

---

## ⚙️ Temporal Setup (Local Dev)

### 1. Download and extract the Temporal CLI binary

```bash
wget -O temporal_linux_arm64.tar.gz "https://temporal.download/cli/archive/latest?platform=linux&arch=arm64"
tar -xvzf temporal_linux_arm64.tar.gz
```

### 2. Start the Temporal server (with UI)

```bash
./temporal server start-dev --ui-public-path /proxy/8233
```

### 3. Open the Temporal UI in your browser

Visit the following URL, replacing `<your-cloud9-subdomain>`:

```
https://<your-cloud9-subdomain>.cloudfront.net/proxy/8233/
```

---

## ☕ Java Setup (OpenJDK 17)

### 1. Install OpenJDK 17 (on Amazon Linux)

```bash
sudo amazon-linux-extras enable corretto17
sudo yum clean metadata
sudo yum install -y java-17-amazon-corretto-devel
```

### 2. Verify Java installation

```bash
java -version
```

Expected output:

```
openjdk version "17" ...
```

> 🛡️ **Note:**
> The `sudo` password can be found on the **event dashboard page** and is the same one used to launch VS Code.

---

## ▶️ Steps to Execute

1. **Run `WorkerTest`**
   Validates connectivity with Amazon Bedrock and ensures model access is working correctly.

2. **Run `WorkflowApp`**
   Starts a recurring workflow that is scheduled to run every minute.

3. **Confirm the workflow is running**
   Open the Temporal dashboard:

   ```
   https://<your-cloud9-subdomain>.cloudfront.net/proxy/8233/
   ```

4. **Run `WorkerApp`**
   Executes the agentic workflow that polls the webpage and generates meaningful change summaries using Bedrock.

---

## 🔗 Code Repositories

* 💻 **Java implementation:** [GitHub - My Agent Smith (Java)](https://github.com/sudarshan89/my-agent-smith)
* 🐍 **Python version (coming soon):** *\[link to be added when available]*

---

## 🧠 Prompt Engineering Examples

Here are a few prompt patterns that generate useful outputs in this app:

* **Diff summarisation:**

  ```
  Compare the following two versions of content and generate a high-level summary of meaningful differences:
  --- PREVIOUS VERSION ---
  [previous HTML/text content]
  --- CURRENT VERSION ---
  [current HTML/text content]

  Respond in plain English with the key updates developers should know about.
  ```

* **Generic update summary:**

  ```
  Summarise the changes between these two versions of a product webpage. Focus only on material updates.
  ```

These prompts are designed to work well with Claude and Titan foundation models via Amazon Bedrock.

---

@TODO -
Add a skeleton repo, pom.xml + WorkerTest.java
Prompts that generate the app