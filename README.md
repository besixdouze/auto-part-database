# Auto Part Database (Java TCP client/server)

> University course project (CPEN 410, Mobile, Web and Internet Programming, UAGM, Fall 2025). Team of 2 students.
> The assignment required hand-built data structures (no `java.util` collections for the core storage) and a console client, so there is no GUI.

A multi-threaded TCP server that stores a car-parts catalog in memory, and a console client that talks to it over a small binary protocol. The server keeps car subsystems (engine, brakes, ...) in a binary search tree, the products of each subsystem in a sorted circular doubly linked list, and each product's distributors in a sorted singly linked list.

## What it does
- Add a car subsystem
- Add a product to a subsystem, with its distributors
- Modify a product's price and stock
- List all subsystems (alphabetical)
- List all products grouped by subsystem
- List the products and distributors of one subsystem
- Buy a product (decreases stock, refuses when out of stock)
- Several clients can connect at the same time (one thread per connection)

## Data structures
| Structure | Where | Order |
|---|---|---|
| Binary search tree | `AutoPartDatabase` / `SubsystemNode`: subsystems | by title, case-insensitive; in-order traversal gives alphabetical listing |
| Circular doubly linked list | `Product`: products inside a subsystem | by product name |
| Singly linked list | `Distributor`: distributors of a product | by company name |

## Project layout
```
src/edu/suagm/soe/autopart/
  AutoPartServer.java     accepts connections, starts one AutoPartHandler thread per client
  AutoPartHandler.java    server side of the protocol, one instance per client
  AutoPartClient.java     console menu client
  AutoPartCodes.java      port, buffer size, command and error codes
  AutoPartDatabase.java   BST of subsystems (synchronized methods)
  SubsystemNode.java      BST node, owns the product list
  Product.java            list node, owns the distributor list
  Distributor.java        list node
```

## Build and run
Requires JDK 8 or newer. From the repository root:

```
javac -d out src/edu/suagm/soe/autopart/*.java
```

Start the server (default port 9000, or pass another port):
```
java -cp out edu.suagm.soe.autopart.AutoPartServer
java -cp out edu.suagm.soe.autopart.AutoPartServer 9100
```

Start a client in another terminal (default `localhost:9000`):
```
java -cp out edu.suagm.soe.autopart.AutoPartClient
java -cp out edu.suagm.soe.autopart.AutoPartClient localhost 9100
```

Data is kept in memory only, so the catalog starts empty every time the server starts. Add a subsystem first (menu option 1), then products.

## Protocol
Each request starts with an `int` command code (`DataOutputStream.writeInt`). Strings use `writeUTF`, numbers use `writeDouble` / `writeInt`. For most commands the server first replies `OK` and then the client sends the arguments.

| Code | Command | | Code | Error |
|---|---|---|---|---|
| 2 | ADD_SUBSYSTEM | | 20 | SUBSYSTEM_NOT_FOUND |
| 3 | ADD_PRODUCT | | 21 | PRODUCT_NOT_FOUND |
| 4 | MODIFY_PRODUCT | | 22 | OUT_OF_STOCK |
| 5 | LIST_SUBSYSTEMS | | 30 | WRONGCOMMAND |
| 6 | LIST_PRODUCTS_BY_SUBSYSTEM | | 1 | OK |
| 7 | LIST_PRODUCTS_FOR_SUBSYSTEM | | | |
| 8 | BUY_PRODUCT | | | |
| 9 | EXIT | | | |

## Example session
Output from running the client against the server (trimmed), after adding two subsystems and two products:

```
--- All Products by Subsystem ---

=== Brakes ===
Name: Brake Pad Set
Description: Ceramic front pads
UPC: 036000291452
Manufacturer: Wagner
Price: $45.50
Quantity in Stock: 1
Distributors:
  - Parts Depot (Contact: Ana Diaz, Phone: 787-555-0103)

=== Engine ===
Name: Spark Plug
Description: Iridium spark plug
UPC: 012345678905
Manufacturer: NGK
Price: $8.99
Quantity in Stock: 2
Distributors:
  - Advance Parts (Contact: Juan Rivera, Phone: 787-555-0102)
  - Autozone Supply (Contact: Maria Lopez, Phone: 787-555-0101)
```
The names, UPCs and phone numbers above are made-up test data.

## Known limitations (this is a course project)
- Storage is in memory only. Restarting the server loses all data.
- Subsystem operations are synchronized, but buying and modifying a product are not. Two clients buying the last item at the same moment could both succeed.
- The tree is not self-balancing, so inserting subsystems in alphabetical order degrades it to a linked list.
- Two products with the same name in one subsystem are not rejected; lookups return the first match.
- No authentication or input validation, and the protocol is unencrypted.
