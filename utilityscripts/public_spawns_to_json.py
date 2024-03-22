import tkinter as tk
from tkinter import filedialog, messagebox, ttk


def process_file(input_file, output_directory, filters):
    print(f"Processing file: {input_file}")
    print(f"Output directory: {output_directory}")
    print(f"Filters: {filters}")
    messagebox.showinfo("Processing", "Processing completed successfully!")


def validate_number_range(a, b):
    return 0 <= a <= b


def select_input_file(input_file_var, input_file_label, output_dir_var, process_button):
    filepath = filedialog.askopenfilename()
    if filepath:
        input_file_var.set(filepath)
        input_file_label.config(text=f"Selected file: {filepath}", foreground="black")
        if filepath and output_dir_var.get():
            process_button['state'] = 'normal'


def select_output_directory(output_dir_var, output_dir_label, input_file_var, process_button):
    directory = filedialog.askdirectory()
    if directory:
        output_dir_var.set(directory)
        output_dir_label.config(text=f"Output directory: {directory}", foreground="black")
        if input_file_var.get() and directory:
            process_button['state'] = 'normal'


def toggle_filter(filter_var, button):
    if filter_var.get():
        filter_var.set(False)
        button.config(style='Deactivated.TButton')
    else:
        filter_var.set(True)
        button.config(style='Active.TButton')


def create_filter_toggle_buttons(filter_category, filter_vars, start_row, parent_frame, max_columns=5):
    row, col = start_row, 0
    for name, var in filter_vars[filter_category].items():
        # Define btn before the lambda function
        btn = ttk.Button(parent_frame, text=name, style='Active.TButton')
        btn.config(command=lambda var1=var, btn1=btn: toggle_filter(var1, btn1))
        btn.grid(column=col, row=row, sticky=tk.W, pady=2, padx=2)
        col += 1
        if col >= max_columns:
            col = 0
            row += 1
    return row


def setup_ui(root):
    # Variables for storing UI inputs
    input_file_var = tk.StringVar()
    output_dir_var = tk.StringVar()
    pokemon_number_start_var = tk.IntVar(value=0)  # Default start of dex range
    pokemon_number_end_var = tk.IntVar(value=1111)  # Default end of dex range

    frame = ttk.Frame(root, padding="10")
    frame.grid()
    ttk.Separator(root, orient='horizontal').grid(row=20, column=0, sticky="ew", pady=5)

    filter_vars, process_button = setup_filter_ui(root, input_file_var, output_dir_var, pokemon_number_start_var,
                                                  pokemon_number_end_var)
    input_file_label, output_dir_label = setup_file_selection_ui(frame, input_file_var, output_dir_var, process_button)

    root.mainloop()


def setup_file_selection_ui(frame, input_file_var, output_dir_var, process_button):
    input_file_button = ttk.Button(frame, text="Select Input File",
                                   command=lambda: select_input_file(input_file_var, input_file_label, output_dir_var,
                                                                     process_button))
    input_file_button.grid(column=0, row=0, pady=5)

    input_file_label = ttk.Label(frame, text="No file selected", foreground='red')
    input_file_label.grid(column=0, row=1, pady=5)

    output_dir_button = ttk.Button(frame, text="Select Output Directory",
                                   command=lambda: select_output_directory(output_dir_var, output_dir_label,
                                                                           input_file_var, process_button))
    output_dir_button.grid(column=0, row=2, pady=5)

    output_dir_label = ttk.Label(frame, text="No directory selected", foreground='red')
    output_dir_label.grid(column=0, row=3, pady=5)

    return input_file_label, output_dir_label


def setup_filter_ui(root, input_file_var, output_dir_var, pokemon_number_start_var, pokemon_number_end_var):

    frame3 = ttk.Frame(root, padding="10")
    frame3.grid(sticky=(tk.W, tk.E, tk.N, tk.S))

    # Label and entry for specifying the range of Pokémon numbers to include
    ttk.Label(frame3, text="Pokémon Number Range:").grid(column=0, row=0, columnspan=5, pady=5)
    ttk.Entry(frame3, textvariable=pokemon_number_start_var, width=5).grid(column=1, row=1, pady=5)
    ttk.Label(frame3, text="-").grid(column=2, row=1, pady=5)
    ttk.Entry(frame3, textvariable=pokemon_number_end_var, width=5).grid(column=3, row=1, pady=5)

    filter_vars = {
        'included_groups': {name: tk.IntVar(value=1) for name in ['basic', 'boss', 'fossil']},
        'known_contexts': {name: tk.IntVar(value=1) for name in ['grounded', 'submerged', 'seafloor', 'surface']},
        'bucket_mapping': {name: tk.IntVar(value=1) for name in ['common', 'uncommon', 'rare', 'ultra-rare']},
        'included_generations': {str(gen): tk.IntVar(value=1) for gen in range(1, 10)}
    }

    # Custom styles for toggle buttons
    style = ttk.Style()
    style.configure('Deactivated.TButton', background='gray', foreground='gray', font=('Helvetica', 10, 'overstrike'))
    style.configure('Active.TButton', background='#4CAF50', foreground='black', font=('Helvetica', 10))

    # Organize filters into sections and create toggle buttons for each
    filter_sections = [
        ('included_groups', "Groups"),
        ('known_contexts', "Contexts"),
        ('bucket_mapping', "Buckets"),
        ('included_generations', "Generations")
    ]

    current_row = 2
    ttk.Label(frame3, text="Toggle the following filters to exclude certain "
                           "categories of cobblemon:").grid(column=0, row=current_row, columnspan=5, pady=5)
    current_row += 1
    for category, label_text in filter_sections:
        ttk.Label(frame3, text=f"{label_text}:").grid(column=0, row=current_row + 1, sticky=tk.W, pady=2)
        current_row += 1
        current_row = create_filter_toggle_buttons(category, filter_vars, current_row + 2, parent_frame=frame3)

    ttk.Separator(frame3, orient='horizontal').grid(row=current_row + 1, column=0, columnspan=5, sticky="ew", pady=5)
    current_row += 1

    process_button = ttk.Button(frame3, text="Execute Spawns Writer",
                                command=lambda: process_file(input_file_var.get(), output_dir_var.get(), filter_vars),
                                state='disabled')
    process_button.grid(column=0, row=current_row + 1, columnspan=5, sticky=(tk.W, tk.E), pady=5)

    return filter_vars, process_button


if __name__ == "__main__":
    root = tk.Tk()
    root.title("Cobblemon Spawn Data Converter")
    setup_ui(root)
