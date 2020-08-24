$(document).ready(function() {
  addSourceRow()
})

function addSourceRow() {
  fetch("/corpuses")
    .then((resp) => resp.json())
    .then((json) => {
      $("#sourceSpecification").append(
        `<div class="form-row">
          <div class="col-md-8">
            <div class="form-group">
              <select class="form-control sourceName">
              </select>
            </div>
          </div>
          <div class="col-md-2">
            <div class="form-group">
              <input class="form-control sourceWeight" type="number" value="1.0" min="0" max="10" step="0.1" />
            </div>
          </div>
          <div class="col-md-2">
            <span class="fas fa-minus-circle" style="font-size: 16px"></span>
            <span class="fas fa-dice" style="font-size: 16px"></span>
          </div>
        </div>`)

      json.forEach(function(corpusName) {
        $(".sourceName").append("<option>" + corpusName + "</option>")
      })

      $(".fa-minus-circle").last()
        .click(function() {
          $(this).closest('.form-row').remove();
        })

      $(".fa-dice").last().click(function() {
        let selectedIndex = Math.floor(Math.random() * json.length);
        $(this).closest('.form-row').find(".sourceName").find("option").eq(selectedIndex).attr("selected", "selected");
        $(this).closest('.form-row').find(".sourceWeight").val(Math.random().toFixed(1))
      })

      $(".fa-dice").last().click();
      $(this).closest('.form-row').find(".sourceWeight").val(1.0)
    })
}

function generate() {
  let sourceSpecs = []
  $("#sourceSpecification").find(".form-row")
    .each((i, element) => {
      sourceSpecs[i] = {
        "filename": $(element).find(".sourceName").val(),
        "weight": $(element).find(".sourceWeight").val()
      }
    });

  let requestBody = JSON.stringify({
    "chunkSize": $("#chunkSize").val(),
    "batchSize": $("#batchSize").val(),
    "minWordLength": $("#minWordLength").val(),
    "maxWordLength": $("#maxWordLength").val(),
    "pattern": $("#pattern").val(),
    "sourceSpecifications": sourceSpecs
  })

  $("#resultsText").html("<p class='notResult'>Generating. Please wait...</p>")

  fetch("/names", {
      method: 'post',
      headers: {
        'Accept': 'application/json, text/plain, */*',
        'Content-Type': 'application/json'
      },
      body: requestBody
    })
    .then((resp) => resp.json())
    .then((json) => {
      $("#resultsText").html("")
      if (json.length == 0) {
        $("#resultsText").append("<p class='notResult'>Couldn't generate any words that meet those criteria. Please adjust your settings and try again.</p>")
      }
      json.forEach(function(name) {
        $("#resultsText").append("<p>" + name + "</p>")
      })
    })
}
