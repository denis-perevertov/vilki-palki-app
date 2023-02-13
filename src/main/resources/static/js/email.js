let loadTemplateButton = document.getElementById('load_template');
let templateFileInput = document.getElementById('html_download');

loadTemplateButton.addEventListener('click', function() {
    templateFileInput.click();
})

templateFileInput.addEventListener('change', function() {
    let span = document.getElementById('loaded_file');
    let fileName = this.files[0].name;
    span.innerText = fileName;
    span.style.color = "red";

    let templateList = document.getElementById('template_list');
    templateList.innerHTML += '<input type="radio" name="template" onclick="setTemplate(this)" value="'+fileName+'">'+fileName+'<br>';
});

function setTemplate(input) {
    let span = document.getElementById('chosen_template');
    span.innerText = input.value;
    span.style.color = "red";
}