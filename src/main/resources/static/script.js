const editModal = document.getElementById('editModal')
if (editModal) {
  editModal.addEventListener('show.bs.modal', event => {
    // Button that triggered the modal
    const button = event.relatedTarget
    // Extract info from data-bs-* attributes
    const destinationName = button.getAttribute('data-bs-whatever')
    //const destinationId =
    // If necessary, you could initiate an Ajax request here
    // and then do the updating in a callback.

    // Update the modal's content.
    const modalBodyInput = editModal.querySelector('.modal-body input')
    modalBodyInput.value = destinationName
    //const modalFooterInput = editModal.querySelector('destinationId')
    //modalFooterInput.value = destinationId
  })
}

