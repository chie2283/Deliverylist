(() => {
    'use strict'

    const buttons = document.querySelectorAll('.edit-button');
    const destinationId = document.getElementById('destinationId');
    const destinationName = document.getElementById('destinationName');
    const isEdit = document.getElementById('isEdit');

    for(const button of buttons) {
        button.addEventListener('click', event => {
            const el = event.target;
            console.log(el.dataset.destinationId);

            fetch(`/destination/${el.dataset.destinationId}`)
            .then(response => response.json())
            .then(result => {
                destinationId.value = result.id;
                destinationName.value = result.name;
            })

            .catch(error => {
                console.error('Error:', error);
            });
            isEdit.value = "true";
        })
    }

    document.getElementById('addButton').addEventListener('click', event => {
        destinationId.value = -1;
        destinationName.value = "";
        isEdit.value = "false";
    });
})()