# MiniVCS - A Minimal Version Control System

## Overview

MiniVCS is a simple, educational implementation of a version control system inspired by Git. This project aims to teach the core concepts of how Git works by building a minimalist version from scratch.

## Learning Goals

- Understanding Git's internal data structures and algorithms
- Learning about content-addressable storage
- Implementing basic version control operations
- Working with file system operations in Java

## Core Concepts to Implement

### 1. Repository Structure

- `.minivcs/` directory for storing all version control data
- `.minivcs/objects/` for storing content-addressable file snapshots
- `.minivcs/refs/` for storing references (branches, HEAD)
- `.minivcs/index` for tracking staged changes

### 2. Data Model

- **Blob**: Represents file content
- **Tree**: Represents directory structure
- **Commit**: Represents a snapshot with metadata
- **Index/Staging Area**: Tracks files to be committed

### 3. Commands to Implement

#### Basic Commands

- `init`: Initialize a new repository

  - Create the `.minivcs` directory structure
  - Set up initial index

- `add <file>`: Stage file(s) for commit

  - Read file content
  - Calculate hash (SHA-1)
  - Store in objects directory
  - Update index

- `status`: Show working tree status

  - Compare working directory with index
  - Show modified, new, and deleted files

- `commit -m <message>`: Record changes
  - Create tree objects for directories
  - Create commit object with parent reference
  - Update HEAD reference

#### Intermediate Commands

- `log`: Show commit history

  - Traverse commit history
  - Display commit messages and metadata

- `rm <file>`: Remove file from working directory and index

  - Update index to remove file
  - Handle file deletion if needed

- `diff <file>`: Show changes between working directory and index
  - Compare file content
  - Generate and display unified diff format

#### Advanced Features (Optional)

- `branch <name>`: Create a new branch
- `checkout <branch>`: Switch branches
- `merge <branch>`: Merge changes from another branch

## Implementation Steps

1. **Setup Project Structure**

   - Create command-line interface
   - Define basic classes

2. **Implement Core Storage**

   - Object storage system
   - Index file format
   - Reference management

3. **Implement Basic Commands**

   - Start with init, add, status
   - Move to commit, log
   - Then implement diff, rm

4. **Add Documentation**
   - Document internal workings
   - Compare with Git's approach

## Technical Requirements

- File I/O for reading and writing repository data
- Content hashing (SHA-1) for object identifiers
- Serialization for storing objects
- Directory traversal and monitoring
- Diff generation algorithms

## Tips for Implementation

- Start small - get basic functionality working first
- Test each command thoroughly before moving to the next
- Use simple data structures initially
- Consider edge cases (empty files, binary files, etc.)
- Look at Git's source for inspiration, but implement your own approach

## Resources

- [Git Internals](https://git-scm.com/book/en/v2/Git-Internals-Plumbing-and-Porcelain)
- [Building Git from Scratch](https://wyag.thb.lt/)
- [Content-Addressable Storage](https://en.wikipedia.org/wiki/Content-addressable_storage)
- [Merkle Trees](https://en.wikipedia.org/wiki/Merkle_tree)
