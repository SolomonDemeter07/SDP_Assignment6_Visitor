# Assignment 6 — Visitor Pattern (Advanced File System)

## Overview
This project implements an in-memory hierarchical file system in **Java 17** using the **Visitor** behavioral design pattern. It supports three independent operations over `FileNode` and `DirectoryNode` elements via double dispatch without using `instanceof`, `getClass()`, reflection, or type casts:
* `TotalSizeVisitor` — calculates the total byte size of all files in a visited subtree.
* `SearchVisitor` — filters files by a substring criterion and minimum byte size threshold (`size >= threshold`), returning lexicographically sorted paths relative to the visited root using `/` as separator.
* `StatisticsVisitor` — counts visited files, directories (including the visited root if it is a directory), and computes maximum tree depth (where root depth = 0).

## Design & Architecture Decisions
* **Traversal Ownership:** Traversal is owned by the **visitors** (`visit(DirectoryNode)` iterates over `directory.getChildren()`). This keeps `FileNode` and `DirectoryNode` strictly focused on tree invariants and single-step double dispatch (`visitor.visit(this)`), while allowing visitors to naturally track traversal depth and relative path prefixes.
* **Controlled State & Reusability:** Visitor instances are **reusable**. Each visitor tracks `activeTraversalDepth` and automatically resets its internal accumulators when a new top-level traversal starts (`activeTraversalDepth == 0`), preventing stale state or multiplied results across repeated runs.
* **Tree Integrity:** `DirectoryNode` enforces unique sibling names, single-parent ownership, and cycle prevention (walking up parent references) while exposing children only as an unmodifiable list.

## Prerequisites
* **Java:** JDK 17
* **Build Tool:** Apache Maven 3.8+

## Exact Commands to Run Tests and Application
### 1. Run Automated JUnit 5 Tests (T1–T8):
mvn test

### 2. Run Executable Demonstration (`Main`):
mvn compile exec:java

## Sample Output
=== 1. Total Size Operation ===
Total subtree size: 21000 bytes

=== 2. Search Operation (filter='.java', minSize=2500) ===
Matched: src/core/BenchPressTracker.java
Matched: src/core/SquatCalculator.java

=== 3. Statistics Operation ===
Files: 6
Directories: 6
Max Depth: 2