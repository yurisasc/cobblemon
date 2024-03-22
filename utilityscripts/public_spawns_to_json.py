import tkinter as tk
from tkinter import filedialog, messagebox, ttk

def main():
    # Init helper functions:

    # Function to simulate script's main processing logic
    def process_file(input_file, output_directory, filters):
        print(f"Processing file: {input_file}")
        print(f"Output directory: {output_directory}")
        print(f"Filters: {filters}")
        messagebox.showinfo("Processing", "Processing completed successfully!")

    def validate_number_range(a, b):
        # Example validation function that could be expanded as needed
        return 0 <= a <= b

    # Function to open a file picker dialog and update the label
    def select_input_file():
        filepath = filedialog.askopenfilename()
        if filepath:
            input_file_var.set(filepath)
            input_file_label.config(text=f"Selected file: {filepath}", foreground="black")
            # Check if both input and output have been selected
            if filepath and output_dir_var.get():
                process_button['state'] = 'normal'

    def select_output_directory():
        directory = filedialog.askdirectory()
        if directory:
            output_dir_var.set(directory)
            output_dir_label.config(text=f"Output directory: {directory}", foreground="black")
            # Check if both input and output have been selected
            if input_file_var.get() and directory:
                process_button['state'] = 'normal'

    # Toggle button function
    def toggle_filter(filter_var, button):
        if filter_var.get():
            filter_var.set(False)
            button.config(style='Deactivated.TButton')  # Default style for deactivated
        else:
            filter_var.set(True)
            button.config(style='Active.TButton')  # Custom style for activated
        # print current filter vars
        print(filter_vars)

    def create_filter_toggle_buttons(filter_category, filter_vars, start_row, parent_frame, max_columns=5):
        row, col = start_row, 0
        for name, var in filter_vars[filter_category].items():
            btn = ttk.Button(parent_frame, text=name, style='Active.TButton')
            btn['command'] = lambda var1=var, btn1=btn: toggle_filter(var1, btn1)
            btn.grid(column=col, row=row, sticky=tk.W, pady=2, padx=2)
            filter_buttons[name] = btn
            col += 1
            if col >= max_columns:
                col = 0
                row += 1
        return row

    # Script main function
    # Setup Tkinter window
    root = tk.Tk()
    root.title("Cobblemon Spawn Data Converter")

    # Configure the grid layout
    input_file_var = tk.StringVar()
    output_dir_var = tk.StringVar()
    root.columnconfigure(0, weight=1)
    root.rowconfigure(0, weight=1)

    # Create a frame for the content
    frame = ttk.Frame(root, padding="10")
    frame.grid(sticky=(tk.W, tk.E, tk.N, tk.S))

    # Variables for storing UI inputs

    pokemon_number_start_var = tk.IntVar(value=0)  # Default start of dex range
    pokemon_number_end_var = tk.IntVar(value=1111)  # Default end of dex range





    # Filter variables - Using IntVar for checkboxes (1 = checked, 0 = unchecked)
    filter_vars = {}

    # UI Elements
    input_file_button = ttk.Button(frame, text="Select Input File", command=select_input_file)
    input_file_button.grid(column=0, row=0, sticky=(tk.W, tk.E), pady=5)

    input_file_label = ttk.Label(frame, text="No file selected", foreground='red')
    input_file_label.grid(column=0, row=1, sticky=(tk.W), pady=5)

    output_dir_button = ttk.Button(frame, text="Select Output Directory", command=select_output_directory)
    output_dir_button.grid(column=0, row=2, sticky=(tk.W, tk.E), pady=5)

    output_dir_label = ttk.Label(frame, text="No directory selected", foreground='red')
    output_dir_label.grid(column=0, row=3, sticky=(tk.W), pady=5)

    # UI for Pokemon Number Range
    ttk.Label(frame, text="Pokémon Number Range (inclusive):").grid(column=0, row=5, sticky=tk.W, pady=2)
    ttk.Entry(frame, textvariable=pokemon_number_start_var).grid(column=0, row=6, sticky=tk.W, pady=2)
    ttk.Entry(frame, textvariable=pokemon_number_end_var).grid(column=1, row=6, sticky=tk.W, pady=2)

    # Create custom styles for toggle buttons
    style = ttk.Style()
    style.configure('Deactivated.TButton', background='gray', foreground='gray', font=('Helvetica', 10, 'overstrike'))
    style.configure('Active.TButton', background='#4CAF50', foreground='black', font=('Helvetica', 10))
    style.configure('TButton', padding=5)

    # Filter variables and button references
    filter_buttons = {}




    # Visual splitter and filter categories heading
    separator = ttk.Separator(frame, orient='horizontal')
    separator.grid(column=0, row=7, columnspan=5, sticky='ew', pady=10)

    # Create a new frame for the content below the separator
    frame2 = ttk.Frame(root, padding="10")
    frame2.grid(sticky=(tk.W, tk.E, tk.N, tk.S))

    # Define your filter categories with names and default values
    filter_vars['included_groups'] = {name: tk.IntVar(value=1) for name in ['basic', 'boss', 'fossil']}
    filter_vars['known_contexts'] = {name: tk.IntVar(value=1) for name in ['grounded', 'submerged', 'seafloor', 'surface']}
    filter_vars['bucket_mapping'] = {name: tk.IntVar(value=1) for name in ['common', 'uncommon', 'rare', 'ultra-rare']}
    filter_vars['included_generations'] = {str(gen): tk.IntVar(value=1) for gen in [1, 2, 3, 4, 5, 6, 7, 8, 9]}

    ttk.Label(frame2,
              text="Filter the spreadsheet to only generate data for:").grid(
        column=0, row=0, columnspan=5)
    # Arrange filters in a row-wise layout in the new frame
    ttk.Label(frame2, text="Groups:").grid(column=0, row=1, sticky=tk.W)
    next_row = create_filter_toggle_buttons('included_groups', filter_vars, 2, parent_frame=frame2)

    ttk.Label(frame2, text="Contexts:").grid(column=0, row=next_row + 1, sticky=tk.W)
    next_row = create_filter_toggle_buttons('known_contexts', filter_vars, next_row + 2, parent_frame=frame2)

    ttk.Label(frame2, text="Buckets:").grid(column=0, row=next_row + 1, sticky=tk.W)
    next_row = create_filter_toggle_buttons('bucket_mapping', filter_vars, next_row + 2, parent_frame=frame2)

    ttk.Label(frame2, text="Generations.:").grid(column=0, row=next_row + 1, sticky=tk.W)
    create_filter_toggle_buttons('included_generations', filter_vars, next_row + 2, max_columns=5, parent_frame=frame2)

    # Create the Process button at the very bottom, initially disabled
    process_button = ttk.Button(frame2, text="Execute Spawns Writer with these settings",
                                command=lambda: process_file(input_file_var.get(), output_dir_var.get(), filter_vars),
                                state='disabled')
    # Adjust the row index to place it at the bottom, ensure it's after all other UI elements
    process_button.grid(column=0, row=20, columnspan=5, sticky=(tk.W, tk.E), pady=5)
    root.mainloop()






if __name__ == "__main__":
    main()