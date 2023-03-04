

function drawLineChart(date_from, date_to, users_dates) {

    let line_ctx = document.getElementById('users-chart');

    const time_intervals = [];

    let current = new Date(date_from);
    let end = new Date(date_to);

    console.log("current date: " + current);
    console.log("end date: " + end);


    do {
        let d1 = new Date(current);
        let d2 = new Date(current.setDate(current.getDate() + 30));
        let count = 0;

        for(let i = 0; i < users_dates.length; i++) {
            let user_date = new Date(users_dates[i]);
            if(user_date > d1 && user_date < d2) {
                console.log("date " + user_date + " is within the interval ");
                count += 1;
            }
        }

        time_intervals.push({
            start:d1.toISOString().split('T')[0],
            end:d2 <= end ? d2.toISOString().split('T')[0] : end.toISOString().split('T')[0],
            date_counts:count
        });

    } while (current <= end);

    console.log(time_intervals);

    const line_chart_labels = [];
    const line_chart_data = [];

    for(let i = 0; i < time_intervals.length; i++) {
        line_chart_labels.push(time_intervals[i].end);
        line_chart_data.push(time_intervals[i].date_counts);
    }

    console.log(line_chart_labels);
    console.log(line_chart_data);

    new Chart(line_ctx, {
      type: 'line',
      data: {
      labels: time_intervals.map(row => row.end),
        datasets: [{
          label: 'Новые пользователи за интервал: ',
          data: time_intervals.map(row => row.date_counts),
          borderWidth: 1,
          fill: false,
          borderColor: 'rgb(75, 192, 192)',
          tension: 0.1
        }]
      },
      options: {
        plugins: {legend: {display: false}},
        scales: {
          y: {
            beginAtZero: true
          }
        }
      }
    });
}

function drawBarChart(date_from, date_to, orders) {

    console.log("ORDERS FOR DRAWING CHART: ");
    console.log(orders);

    let bar_ctx = document.getElementById('sales-chart');

    const time_intervals = [];

    let current = new Date(date_from);
    let end = new Date(date_to);

    console.log("current date: " + current);
    console.log("end date: " + end);

    do {
        let d1 = new Date(current);
        let d2 = new Date(current.setDate(current.getDate() + 30));

        console.log("d1 date: " + d1);
        console.log("d2 date: " + d2);

        for(let i = 0; i < orders.length; i++) {
            let order = orders[i];
            console.log(order.datetime);
            console.log(order.datetime.split('T')[0]);
            let orderDate = new Date(order.datetime.split('T')[0]);

            console.log(order);
            console.log(orderDate);

            let count = 0;

            if(orderDate > d1 && orderDate < d2) {
                console.log("date " + orderDate + " is within the interval ");
                let orderPrice = 0;
                for(let j = 0; j < order.itemList.length; j++) {
                    orderPrice += order.itemList[j];
                }
                console.log("Order price of the order (id=" + order.id + ") = " + orderPrice);
                count += orderPrice;
            }
        }

        time_intervals.push({
            start:d1.toISOString().split('T')[0],
            end:d2 <= end ? d2.toISOString().split('T')[0] : end.toISOString().split('T')[0],
            total_price:count
        });

    } while (current <= end);

    console.log(time_intervals);

    const bar_chart_labels = [];
    const bar_chart_data = [];

    for(let i = 0; i < time_intervals.length; i++) {
        bar_chart_labels.push(time_intervals[i].end);
        bar_chart_data.push(time_intervals[i].total_price);
    }

    console.log(bar_chart_labels);
    console.log(bar_chart_data);

    const bar_data = {
        labels: time_intervals.map(row => row.end),
        datasets: [{
          label: 'Продажи за интервал: ',
          data: time_intervals.map(row => row.total_price),
          backgroundColor: [
            'rgba(255, 99, 132, 0.2)',
            'rgba(255, 159, 64, 0.2)',
            'rgba(255, 205, 86, 0.2)',
            'rgba(75, 192, 192, 0.2)',
            'rgba(54, 162, 235, 0.2)',
            'rgba(153, 102, 255, 0.2)',
            'rgba(201, 203, 207, 0.2)'
          ],
          borderColor: [
            'rgb(255, 99, 132)',
            'rgb(255, 159, 64)',
            'rgb(255, 205, 86)',
            'rgb(75, 192, 192)',
            'rgb(54, 162, 235)',
            'rgb(153, 102, 255)',
            'rgb(201, 203, 207)'
          ],
          borderWidth: 1
        }]
    };

    const bar_config = {
      type: 'bar',
      data: bar_data,
      options: {
        scales: {
          y: {
            beginAtZero: true
          }
        }
      },
    };

    new Chart(bar_ctx, bar_config);
}
