# A Better Bill Split App: Design Doc

## Architecture Overview
We will be implementing a bill splitting app where users can upload a picture of their receipt, claim items, and view the exact amount they are responsible for. The key functionalities for the app include:
- A group leader can upload a photo of their receipt.
- Users can see their portion of the bill calculated after claiming items from the receipt.

We will use the following tech stack:
- **Frontend:** Next.js
- **Backend:** GraphQL APIs with Spring Boot framework
- **Message Queue:** Redis
- **Database:** PostgreSQL with Supabase

### System Diagram
![system-diagram.png](system-diagram.png)

Our architecture is designed to handle asynchronous processing, OCR, and real-time concurrency. 
The pros and cons of our design choices are as follows:
- **Frontend:** Next.js 
  - **Pros:** It enables a fast, responsive UI for client-side rendering. This allows users a smooth process of claiming and unclaiming items. 
  - **Cons:** Keeping the frontend state consistent across multiple users in real time increases concurrency requirements and adds complexity to the system design, especially when session data changes often.
- **Backend:** GraphQL APIs with Spring Boot 
  - **Pros:** GraphQL allows the client to request and retrieve the necessary data they need without underfetching or overfetching. 
  - **Cons:** Since the server is stateless, every user interaction, such as a call to claim and unclaim an item, triggers a write to the database. This increases the load on the database and requires using atomic operations when manipulating it to ensure concurrency.


### Sequence Diagrams

## Database Schema

## State Model

## API Design

## Failure Modes