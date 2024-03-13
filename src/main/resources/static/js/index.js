setTimeout(function () {
    $('#alternating').fadeOut(function () {
        $(this).html(", ce-aș juca ceva!").fadeIn();
    });
}, 3000)