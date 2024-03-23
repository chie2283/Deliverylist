function hideForm() {
    document.getElementById("destinationEditDialog").style.display = "none";
}

function showForm() {
    document.getElementById("destinationEditDialog").style.display = "block";
}

window.onload = function() {hideForm();};

document.getElementById("edit").onclick = function edit() {
    const name = document.getElementById("destinationName").value;
};
