
    function addIngredient(slot) {

        var modal = document.getElementById("myModal");
        var modal_content = document.getElementsByClassName("mod-b")[0];
        modal.style.display = "none";
        modal_content.removeChild(slot);
        console.log(slot);

        var ingredients = document.querySelector("div .ingredients");
        console.log(ingredients);

        slot.children[1].setAttribute('name', 'ingredients_names');

        slot.removeEventListener("click", () => addIngredient(slot));
        slot.addEventListener("click", () => removeFromList(slot));

        ingredients.appendChild(slot);
    }

    function removeFromList(slot) {
        var ingredients = document.querySelector("div .ingredients");
        console.log(ingredients);

        ingredients.removeChild(slot);
    }


    $(document).ready(function() {

        var modal = document.getElementById("myModal");
        var modal_content = document.getElementsByClassName("mod-b")[0];
        var btn = document.getElementsByClassName("plus")[0];
        var span = document.getElementsByClassName("close")[0];

        //AJAX-запрос, наполнение модального окна
        $.ajax("http://localhost:8080/api/v3/ingredients", {
            dataType: "json",
            success: function(data) {
                console.log(data);
                modal_content.innerHTML = "";
                modal_content.style.display = "flex";
                for(let i = 0; i < data.length; i++) {
                    let ingredient = data[i];
                    let ingredientSlot = '<div onclick="addIngredient(this)" class="ingredient">'
                    + '<img src="/images/ingredients/'+ingredient.icon+'">' + '<p>'+ingredient.name+'</p>'
                    + '<input type="text" style="display:none;" name="ingredients" value="' + ingredient.id + '"></div>';
                    modal_content.innerHTML += ingredientSlot;
                }
            }
        });

        btn.onclick = function() {
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
    });







    // --Ингредиенты--

