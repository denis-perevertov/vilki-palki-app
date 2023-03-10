
function createCard(item){
    let item_card = document.createElement('a');
    item_card.classList.add('item');
    console.log(item);
    if(item.category != null) console.log(item.category.id);
    item_card.setAttribute('href', 'items/' + item.id);
    let pictureName = (item.pictureFileName === null || item.pictureFileName === 'null' || item.pictureFileName === '') ?
     'placeholder.jpg' : item.pictureFileName;
    console.log(item.pictureFileName);
    console.log(pictureName);
    item_card.innerHTML = '<img src="/vilkipalki/images/'+pictureName+'" alt="">'
                                 +'<div>'
                                 +  '<p>'+item.name+'</p>'
                                 +  '<p class="bold">'+item.price+'</p>'
                                 +  '<hr class="divider">'
                                 +  '<p>Вес: '+item.weight+'</p>'
                                 +  '<p>Жиры: '+item.fats+'</p>'
                                 +  '<p>Углеводы: '+item.carbons+'</p>'
                                 +  '<p>Белки: '+item.proteins+'</p>'
                                 +'</div>'
    return item_card;
}

$(document).ready(function() {

    $(".category").click(function() {

        let id = $(this).find("span").text();
        console.log("ID OF CATEGORY = " + id);

        $.ajax("http://localhost:8080/vilkipalki/api/v3/items/categories/" + id + "/items", {
            datatype: "json",
            contentType: "json",

            success: function(data) {
                console.log(data);

                let item_list = document.createElement('div');
                item_list.classList.add('item_list');

                for(let i = 0; i < data.length; i++) {
                    let card = createCard(data[i]);
                    item_list.appendChild(card);
                }

                let back_button = document.createElement('a');
                back_button.innerHTML = '<button class="back_button" type="button">← Назад ←</button>';
                back_button.setAttribute('href', "items");

                let edit_button = document.createElement('a');
                edit_button.innerHTML = '<button class="back_button" type="button">Редактировать категорию</button>';
                edit_button.setAttribute('href', "items/categories/"+id);

                let promise = new Promise(function(resolve, reject) {
                    $(".content .container-fluid").fadeOut("slow");
                    setTimeout(() => {
                        resolve("result")
                    }, 500)

                });

                promise
                  .then(
                    result => {
                      // первая функция-обработчик - запустится при вызове resolve
                      $(".content .container-fluid").html(item_list);
                      $(".content .container-fluid").append(back_button);
                      $(".content .container-fluid").append(edit_button);
                      $(".content .container-fluid").fadeIn("fast");
                    },
                    error => {
                      // вторая функция - запустится при вызове reject
                      alert("Rejected: " + error); // error - аргумент reject
                    }
                  );

            }
        });

    });

});