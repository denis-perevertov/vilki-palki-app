    function newIngredient(div) {

           console.log("ALO");

           let parentDiv = div.parentElement;

           parentDiv.innerHTML = "";

           parentDiv.style.display = "block";

           parentDiv.innerHTML += ('Имя: <input type="text"><br><br>');
           parentDiv.innerHTML += ('Фото: <input type="file"><br><br>');
           parentDiv.innerHTML += ('<button id="add-ingredient" onclick="addIngredient(this.parentElement)" type="button">Сохранить</button>');

    }


    $(document).ready(function() {

        // Get the modal
        var modal = document.getElementById("myModal");

        var modal_content = document.getElementsByClassName("mod-b")[0];

        // Get the button that opens the modal
        var btn = document.getElementsByClassName("plus")[0];

        // Get the <span> element that closes the modal
        var span = document.getElementsByClassName("close")[0];

        // When the user clicks the button, open the modal
        btn.onclick = function() {

          //AJAX-запрос, наполнение модального окна
          $.ajax("http://localhost:8080/api/v3/ingredients", {
            dataType: "json",
            success: function(data) {
                modal_content.innerHTML = "";
                modal_content.style.display = "flex";
                let plusSlot = '<div onclick="newIngredient(this)" class="plus">+</div>'
                for(let i = 0; i < data.length; i++) {
                    let ingredient = data[i];
                    let ingredientSlot = document.createElement("div");
                    ingredientSlot.classList.add("ingredient");
                    ingredientSlot.innerHTML += '<img src="'+ingredient.icon+'">';
                    ingredientSlot.innerHTML += '<p>'+ingredient.name+'</p>';
                    modal_content.appendChild(ingredientSlot);
                }
                modal_content.innerHTML += plusSlot;
            }
          })

          modal.style.display = "block";
          modal.style.opacity = 1;
        }

        // When the user clicks on <span> (x), close the modal
        span.onclick = function() {
          modal.style.display = "none";
        }

        // When the user clicks anywhere outside of the modal, close it
        window.onclick = function(event) {
          if (event.target == modal) {
            modal.style.display = "none";
          }
        }


        $("#myModal .plus").click(newIngredient($(this)));
    });

    function addIngredient(form) {
        console.log(form.children);
        let text_input = form.children[0];
        let img_input = form.children[3];

        let blob;

        const file = img_input.files[0];
        const reader = new FileReader();
        reader.onload = function(e) {
            blob = new Blob([new Uint8Array(e.target.result)], {type: file.type});
            console.log(blob);
        };
        reader.readAsArrayBuffer(file);

        let text = text_input.value;
        let image = reader.result;

        $.ajax({
            type: "POST",
            url: "http://localhost:8080/api/v3/ingredients/add",
            dataType: "json",
            data: JSON.stringify({'name': text, 'icon': image}),
            contentType: "application/json; charset=utf-8",
            success: alert("success"),
        });

    }





    // --Ингредиенты--

