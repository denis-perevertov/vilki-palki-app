
    $(document).ready(function() {
        var saved_ids = [];
        var save_button = $("#save_users");
        var send_button = $("#send-button");

        save_button.click(function() {
            let checkbox_list = document.getElementsByClassName("checkbox");
            for(let i = 0; i < checkbox_list.length; i++) {
                if(checkbox_list[i].checked) saved_ids.push(checkbox_list[i].dataset.userid);
            }
        });

        send_button.click(function() {
            console.log(saved_ids);

            function toObject(arr) {
              var res = {};
              for (var i = 0; i < arr.length; ++i)
                if (arr[i] !== undefined) res[i] = arr[i];
              return res;
            };

            let ids = toObject(saved_ids);
            console.log(ids);

            $.post(
                "/vilkipalki/admin/sms",
                {field:"hello", users:JSON.stringify(saved_ids)},
                function(returnedData) {console.log(returnedData); alert("Отправка SMS здесь не сделана ))");}
            )
            .fail(function() {console.log("error");})
        });
    });

