document.addEventListener('click', function (e) {
    if (e.target && e.target.classList.contains('removeDish')) {
        const row = e.target.closest('tr');
        if (row) row.remove();
    }
});

document.addEventListener('click', function (e) {
    if (e.target && e.target.classList.contains('addDish')) {
        const tableBody = e.target.closest('.content').querySelector('tbody');
        if (tableBody) {
            const newRow = document.createElement('tr');
            newRow.classList.add('dish-row');
            newRow.innerHTML = `
                <td><input type="text" name="newDishName" placeholder="料理名" required></td>
                <td><input type="number" name="newDishPrice" placeholder="価格" required></td>
                <td><button type="button" class="removeDish">削除</button></td>
            `;
            tableBody.appendChild(newRow);
        }
    }
});

document.addEventListener("DOMContentLoaded", function () {
    // ラジオボタンのリストを取得
    const radioButtons = document.querySelectorAll('input[name="category"]');
    // すべてのカテゴリに対応する div を取得
    const categoryDivs = document.querySelectorAll('.content');

    const selectedCategoryName = document.getElementById("selectedCategoryName");

    // ラジオボタンの選択イベントを監視
    radioButtons.forEach(radio => {
        radio.addEventListener('change', () => {
            const selectedValue = radio.value; // 選択されたラジオボタンの値

            const selectedLabel = radio.closest("label").querySelector("span").textContent;
            selectedCategoryName.textContent = selectedLabel;

            // すべての div を非表示にする
            categoryDivs.forEach(div => {
                div.style.display = 'none';
            });

            // 選択された値と一致する id を持つ div を表示
            const matchingDiv = document.getElementById('category-' + selectedValue);
            if (matchingDiv) {
                matchingDiv.style.display = 'block';
            }
        });
    });

    // ページ読み込み時にデフォルトの選択値を反映
    const selectedRadio = document.querySelector('input[name="category"]:checked');
    if (selectedRadio) {
        const selectedValue = selectedRadio.value;
        const matchingDiv = document.getElementById('category-' + selectedValue);
        if (matchingDiv) {
            matchingDiv.style.display = 'block';
        }
    }
});
