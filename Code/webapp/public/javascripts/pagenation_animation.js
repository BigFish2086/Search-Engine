const element = document.querySelector(".pagination ul");
let totalPages = 20;
let page = 1;

element.innerHTML = createPagination(totalPages, page);
function createPagination(totalPages, page) {
  let liTag = '';
  let active;
  let beforePage = page - 1;
  let afterPage = page + 1;

  if (page > 1) {
    liTag += `<li class="btn prev" onclick="createPagination(totalPages, ${page - 1});secondEvent()"><span><i class="fas fa-angle-left"></i> Prev</span></li>`;
  }

  if (page > 2) {
    liTag += `<li class="first numb" onclick="createPagination(totalPages, 1);secondEvent()"><span>1</span></li>`;
    if (page >= 3) {
      liTag += `<li class="dots"><span>...</span></li>`;
    }
  }

  if (page == totalPages) beforePage = beforePage - 2;
  else if (page == totalPages - 1) beforePage = beforePage - 1;
  
  if (page == 1) afterPage = afterPage + 2;
  else if (page == 2) afterPage = afterPage + 1;

  if(beforePage < 1) beforePage = 1;
  if(afterPage > totalPages) afterPage = totalPages;

  for (let plength = beforePage; plength <= afterPage; plength++) {
    if (plength > totalPages) break;
    if (plength == 0) plength = 1;
    if (page == plength) active = "active"; 
    else active = "";
    liTag += `<li class="numb ${active}" onclick="createPagination(totalPages, ${plength});secondEvent()"><span>${plength}</span></li>`;
  }

  // SELECT DISTINCT(word) FROM `word` GROUP by word having count(word) > 3 && count(word) < 40

  if (page < totalPages - 1) { 
    if (page <= totalPages - 2) {
      liTag += `<li class="dots"><span>...</span></li>`;
    }
    liTag += `<li class="last numb" onclick="createPagination(totalPages, ${totalPages});secondEvent()"><span>${totalPages}</span></li>`;
  }

  if (page < totalPages) {
    liTag += `<li class="btn next" onclick="createPagination(totalPages, ${page + 1});secondEvent()"><span>Next <i class="fas fa-angle-right"></i></span></li>`;
  }
  element.innerHTML = liTag;
  return liTag;
}

function replaceQueryParam(param, newval, search) {
  var regex = new RegExp("([?;&])" + param + "[^&;]*[;&]?");
  var query = search.replace(regex, "$1").replace(/&$/, '');
  return (query.length > 2 ? query + "&" : "?") + (newval ? param + "=" + newval : '');
}


function secondEvent() {
  let s = parseInt(document.getElementsByClassName("numb active")[0].innerText);
  let p = window.location.search;
  p = replaceQueryParam("page", s, p);
  window.location = window.location.pathname + p;
}
