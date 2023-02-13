
function createCard(item){
    let item_card = document.createElement('a');
    item_card.classList.add('item');
    console.log(item);
    if(item.category_id != null) console.log(item.category_id.id);
    item_card.setAttribute('href', 'items/' + item.id);
    item_card.innerHTML = '<img src="'+item.pictureFileName+'" alt="">'
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

        $.ajax("http://localhost:8080/api/v3/items/categories/" + id, {
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