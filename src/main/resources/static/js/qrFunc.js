function handleRadioChange(event) {
    const radio = event.target;
    const selectedValue = radio.value; // 選択されたラジオボタンの値

    const selectedSeatName = document.getElementById("selectedSeatName");
    selectedSeatName.textContent = "席番号: " + selectedValue;

    // すべての div を非表示にする
    const categoryDivs = document.querySelectorAll('.contents');
    categoryDivs.forEach(div => {
        div.style.display = 'none';
    });

    // 選択された値と一致する id を持つ div を表示
    const matchingDiv = document.getElementById('contents-' + selectedValue);
    if (matchingDiv) {
        matchingDiv.style.display = 'block';
    }
}

document.addEventListener("DOMContentLoaded", function () {
    // ラジオボタンのリストを取得
    const radioButtons = document.querySelectorAll('input[name="seat"]');

    // ラジオボタンの選択イベントを監視
    radioButtons.forEach(radio => {
        radio.addEventListener('change', handleRadioChange);
    });

    // ページ読み込み時にデフォルトの選択値を反映
    const selectedRadio = document.querySelector('input[name="seat"]:checked');
    if (selectedRadio) {
        const selectedValue = selectedRadio.value;
        const matchingDiv = document.getElementById('contents-' + selectedValue);
        if (matchingDiv) {
            matchingDiv.style.display = 'block';
        }
    }
});