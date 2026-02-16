# A Better Bill Split App: Design Doc

## Architecture Overview
We will be implementing a bill splitting app where users can upload a picture of their receipt, claim items, 
and view the exact amount they are responsible for. The key functionalities for the app include:
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
  - **Pros:** It enables a fast, responsive UI for client-side rendering. This allows users a smooth 
  process of claiming and unclaiming items. 
  - **Cons:** Keeping the frontend state consistent across multiple users in real time increases 
  concurrency requirements and adds complexity to the system design, especially when session data changes often.
- **Backend:** GraphQL APIs with Spring Boot 
  - **Pros:** GraphQL allows the client to request and retrieve the necessary data they need without 
  underfetching or overfetching. 
  - **Cons:** Since the server is stateless, every user interaction, such as a call to claim and 
  unclaim an item, triggers a write to the database. This increases the load on the database and 
  requires using atomic operations when manipulating it to ensure concurrency.

### Sequence Diagrams

#### Description of Receipt Lifecycle
1. The lifecycle of a receipt begins when a group leader uploads an image of the receipt. 
This action creates a session record in the database, generates a unique session_id, and 
sets the parsing_status to pending.
2. The receipt is moved to the message queue for processing. At this time, the UI displays 
a parsing state. While items aren’t yet visible for claiming at this step, other users can 
join the session via a generated QR code provided to the group leader.  
3. Once the receipt parsing service confirms a success status, the itemized data is committed 
to the `items` column in the database, which is of type JSONB. After the database writes the topic
queue broadcasts the new data to the UI. 
4. Multiple users can concurrently claim or unclaim their items. This updates the owner mapping 
in the database in real-time. Since the server is stateless, the database acts as the source of 
truth to prevent multiple users from claiming the same items.
5. The lifecycle ends when the group leader triggers a close session event. The session state 
becomes read-only, and the bill summary is generated for all participants.

#### Receipt Upload Sequence Diagram
![receipt-upload-sequence-diagram.png](receipt-upload-sequence-diagram.png)

#### Item Claim Sequence Diagram
![item-claim-sequence-diagram.png](item-claim-sequence-diagram.png)

## State Model
![state-model.png](state-model.png)

## Database Schema
![data-schema.png](data-schema.png)

Our database schema includes a Sessions table for every session that is created. 
It is initialized with the party size the group leader inputs. To store information about a session efficiently, 
we will be using a document-based database. The Users column stores information about each user in a JSONB data 
format with their userID and their names. Similarly, the Items column uses JSONB to map each item’s itemID to 
its name, price, and owner. To ensure there is smooth communication and error handling between boundaries, 
we added a Parsing column to store the parsing status of the receipt at any given time. After the receipt has 
been sent to the Parsing Service, it will be marked as PENDING. After the Parsing Service sends it to the Topic Queue, 
it will mark it as COMPLETED. If there are any errors, it will be labeled as FAILED. This makes sure that the 
server and UI remain clear about what the receipt’s current status is.

## API Design

## Failure Modes