
# AI-Context Plugin

The **AI-Context Plugin** is an IntelliJ IDEA plugin designed to integrate AI-driven code explanation and processing capabilities directly into your development environment. It leverages a local Ollama instance to provide context-aware code explanations and manages a queue-based system for processing files with AI interactions. This plugin enhances productivity by allowing developers to quickly understand code snippets and manage AI tasks efficiently within the IDE.

## Methodology

Project goal is to work with Grok to handle 95% of the Swing/plugin patterns, keeping as much of the boilerplate and UI nuance out of my head as possible. 
I'm probably learning more about both than intended, but mostly things to avoid and to ask Grok to refactor

## Features

- **Code Context Extraction**: Extracts context from selected code, open files, or the current editor to send to an AI model.
- **AI Interaction**: Communicates with a local Ollama instance (running at `http://localhost:11434`) to explain code or process files.
- **Queue Management**: Allows users to queue files for processing, track their status (Pending, Running, Done, Error, Cancelled), and terminate tasks as needed.
- **Tool Window**: Displays a queue table with file details, prompts, actions (Run/Cancel), status, and processing time, along with results once completed.
- **Customizable Prompts**: Configurable prompt templates via settings to tailor AI interactions (e.g., "Explain this code:").
- **Actions**: Provides context menu actions like "Get Context" to explain selected code, "Queue File" to add files to the queue, and "Run This" for future execution logic.

## Usage

### Prerequisites
- IntelliJ IDEA installed.
- A local Ollama instance running at `http://localhost:11434` with the `llama3` model available.

### Installation
1. Clone or download this repository.
2. Open the project in IntelliJ IDEA.
3. Build the plugin using Gradle (`gradlew buildPlugin`).
4. Install the plugin from disk via IntelliJ's "Install Plugin from Disk" option in the Plugins settings.

### How to Use
1. **Get Context**:
    - Right-click in the editor or on a file in the Project view.
    - Select "Get Context" from the context menu.
    - If text is selected, it will be sent to Ollama for explanation. Otherwise, the plugin uses the current file or all open files as context.
    - Results appear in the AI Context tool window.

2. **Queue a File**:
    - Right-click a file in the Project view.
    - Select "Queue File" to add it to the processing queue.
    - Open the "AI Context" tool window (typically at the bottom of IntelliJ) to view the queue.

3. **Manage the Queue**:
    - In the AI Context tool window, see a table listing queued files with columns: File, Prompt, Action, Status, and Time.
    - Edit the prompt directly in the table if needed.
    - Click "Run" to process a pending file or "Cancel" to stop a running task.
    - Results appear in a row below each file entry once processing completes.

4. **Run Selected Code** (Future Feature):
    - Select code in the editor, right-click, and choose "Run This".
    - Currently displays a dialog with the selected code; execution logic is TBD.

5. **Settings**:
    - Go to `File > Settings > Tools > Ai-Context Settings`.
    - Customize the prompt template (default: "Explain this code:\n").

### Tool Window
- Displays a table with two rows per queued file:
    - **File Row**: Shows the file path, editable prompt, action button (Run/Cancel), status, and elapsed time.
    - **Result Row**: Displays the AI response or error message once processing is complete.

## Planned Next Improvements

- **Abstract Out API Interaction to New Sourceset**: Move Ollama API interaction into a separate module for better modularity and maintainability.
- **Add Jetpack Desktop Client**: Integrate a Jetpack Compose-based desktop client for standalone usage outside IntelliJ.
- **Add CLI Client**: Develop a command-line interface to queue and process files without an IDE.
- **Chooser of Different LLMs for Each Job**: Allow users to select different language models (e.g., beyond `llama3`) per task.
- **Create groups for running processes that direct output files to a shared directory**: Allows jobs of prompts to direct their output to a single location

## Contributing
Feel free to fork this repository, submit issues, or create pull requests to enhance the plugin. Planned improvements are a great starting point for contributions!

## License
This project is licensed under the Apache License, Version 2.0. See the [LICENSE](LICENSE) file for details or visit [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0).