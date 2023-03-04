let loadTemplateButton = document.getElementById('load_template');
let templateFileInput = document.getElementById('html_download');

loadTemplateButton.addEventListener('click', function() {
    templateFileInput.click();
})

templateFileInput.addEventListener('change', function() {
    let span = document.getElementById('loaded_file');
    let fileName = this.files[0].name;

    $.post(
            "/vilkipalki/admin/email/save-template",
            {template_name:fileName},
            function(returnedData) {console.log(returnedData); alert("saved template to DB");}
    ).fail(function() {alert("error")})

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



$(document).ready(function() {

        var saved_ids = [];
        var save_button = $("#save_users");
        var send_button = $("#send-button");
        var chosen_template = "";

        save_button.click(function() {
            let checkbox_list = document.getElementsByClassName("checkbox");
            for(let i = 0; i < checkbox_list.length; i++) {
                if(checkbox_list[i].checked) saved_ids.push(checkbox_list[i].dataset.userid);
            }
        });

        send_button.click(function() {
            console.log(saved_ids);

            chosen_template = $("#chosen_template").text();
            console.log(chosen_template);

            function toObject(arr) {
              var res = {};
              for (var i = 0; i < arr.length; ++i)
                if (arr[i] !== undefined) res[i] = arr[i];
              return res;
            };

            let ids = toObject(saved_ids);
            console.log(ids);

            $.post(
                    "/vilkipalki/admin/email",
                    {users:JSON.stringify(saved_ids), template_name:JSON.stringify(chosen_template)},
                    function(returnedData) {console.log(returnedData); alert("SENT EMAILS TO USERS");}
                  )
                  .fail(function() {console.log("error");})
        });

});