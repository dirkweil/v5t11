function updateElemente(messages) {
  console.log('updateElemente: ' + JSON.stringify(messages));
  for (const message of messages) {
    updateElement(message);
  }
}

function updateElement(message) {
  console.log('updateElement: ' + JSON.stringify(message));

  var element = document.getElementById(message.uiId);
  if (element != null) {
    // console.log('element: ' + element);

    var ctx = element.getContext("2d");

    var aktiveRichtungen = message.a;
    if (aktiveRichtungen != null) {
      console.log('aktiveRichtungen: ' + aktiveRichtungen);

      ctx.beginPath();
      ctx.lineWidth = 30;
      ctx.lineJoin = "round";

      ctx.strokeStyle = message.b ? "red" : "black";

      var start = richtung2point(aktiveRichtungen[0]);
      console.log('start: ' + start.x + '/' + start.y);
      var ende = richtung2point(aktiveRichtungen[1]);
      console.log('ende: ' + ende.x + '/' + ende.y);
      ctx.moveTo(start.x, start.y);
      ctx.lineTo(50, 50);
      ctx.lineTo(ende.x, ende.y);

      ctx.stroke();
    }
    // var gleis = message.gleis;
    // if (gleis != null) {
    //   var gleisName = gleis.name;
    //   var gleisBesetzt = gleis.besetzt;
    //   if (gleisBesetzt)
    //     ctx.fillStyle = "#FF0000";
    //   else
    //     ctx.fillStyle = "#000000";
    //   ctx.font = "30px Arial";
    //   ctx.fillText('G: ' + gleisName, 0, 30);
    // }

    // var weiche = message.weiche;
    // if (weiche != null) {
    //   var weichenName = weiche.name;
    //   var weichenStellung = weiche.stellung;
    //   if (weichenStellung == 'A')
    //     ctx.fillStyle = "#FF0000";
    //   else
    //     ctx.fillStyle = "#000000";
    //   ctx.font = "30px Arial";
    //   ctx.fillText('W: ' + weichenName, 0, 65);
    // }
  }

  function richtung2point(richtung) {
    switch (richtung) {
      case 'N':
        return {x: 50, y: 0};
      case 'NO':
        return {x: 100, y: 0};
      case 'O':
        return {x: 100, y: 50};
      case 'SO':
        return {x: 100, y: 100};
      case 'S':
        return {x: 50, y: 100};
      case 'SW':
        return {x: 0, y: 100};
      case 'W':
        return {x: 0, y: 50};
      default:
        return {x: 50, y: 50};
    }
  }
}
