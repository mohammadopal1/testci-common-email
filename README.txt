# **README: Setting Up and Running EmailTest.java in Eclipse**  

## **Step 1: Set Up Eclipse**  
1. Download and install [Eclipse IDE for Java Developers](https://www.eclipse.org/downloads/).  
2. Launch Eclipse and create a new workspace.  

## **Step 2: Download and Import Source Code**  
1. Download the Apache Commons Email source code:  
   **[Download Link](https://foyzulhassan.github.io/files/commons-email-1.3.2-src.zip)**  
2. Extract the ZIP file.  
3. Open Eclipse → **File** → **Import**.  
4. Select **Existing Projects into Workspace** → Click **Next**.  
5. Click **Browse** and select the extracted folder.  
6. Check **Copy projects into workspace** → Click **Finish**.  

## **Step 3: Set Up JUnit**  
1. Right-click on the project → **Build Path** → **Add Libraries**.  
2. Select **JUnit** → Click **Next** → Choose **JUnit 5** → Click **Finish**.  

## **Step 4: Run JUnit Test**  
1. Navigate to the following path in the imported project:  
   **`commons-email-1.3.2-src/commons-email-1.3.2-src/src/test/java/EmailTest.java`**  
2. Right-click on `EmailTest.java` → **Run As** → **JUnit Test**.
