function handleCheckboxChange(event) {
    const checkbox = event.target;
    const selectedValue = checkbox.value; // 選択されたチェックボックスの値

    const selectedSeatName = document.getElementById("selectedSeatName");

    // チェックされている座席を表示
    let selectedSeats = [];
    const checkedCheckboxes = document.querySelectorAll('input.seat-checkbox:checked');
    checkedCheckboxes.forEach(checkbox => {
        selectedSeats.push(checkbox.value);
    });
    selectedSeatName.textContent = "選択された席番号: " + selectedSeats.join(", ");
}

document.addEventListener("DOMContentLoaded", function () {
    // チェックボックスのリストを取得
    const checkboxes = document.querySelectorAll('input.seat-checkbox');

    // チェックボックスの選択イベントを監視
    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', handleCheckboxChange);
    });

    // ページ読み込み時にデフォルトの選択値を反映
    const selectedCheckboxes = document.querySelectorAll('input.seat-checkbox:checked');
    // 最初の状態で選ばれているチェックボックスの座席番号を表示
    let selectedSeats = [];
    selectedCheckboxes.forEach(checkbox => {
        selectedSeats.push(checkbox.value);
    });
    const selectedSeatName = document.getElementById("selectedSeatName");
    selectedSeatName.textContent = "選択された席番号: " + selectedSeats.join(", ");
});
