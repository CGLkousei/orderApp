document.addEventListener('click', function (e) {
    if (e.target && e.target.classList.contains('removeDish')) {
        const row = e.target.closest('tr');
        if (row) row.remove();
    }

    if (e.target && e.target.type === 'submit') {
        const form = e.target.closest('form');
        if (form) {
            form.submit();  // 明示的にフォームを送信
        }
    }
});

document.addEventListener('click', function (e) {
    if (e.target && e.target.classList.contains('addDish')) {
        const contentDiv = e.target.closest('.content');
        if(contentDiv){
            const idValue = contentDiv.getAttribute('id');
            const categoryId = idValue.replace('category-', '') - 1;

            // 該当カテゴリの `tbody` を取得
            const tableBody = contentDiv.querySelector('tbody');
            if (tableBody) {
                // 現在の行数を取得（新しいインデックスを計算）
                const rowCount = tableBody.querySelectorAll('.dish-row').length;

                // 新しい行を作成
                const newRow = document.createElement('tr');
                newRow.classList.add('dish-row');
                newRow.innerHTML = `
                    <input
                        type="hidden"
                        name="categories[${categoryId}].dishes[${rowCount}].category.id"
                        value="${categoryId + 1}"
                    >
                    <td>
                        <input
                            type="text"
                            name="categories[${categoryId}].dishes[${rowCount}].name"
                            placeholder="料理名"
                            required>
                    </td>
                    <td>
                        <input
                            type="number"
                            name="categories[${categoryId}].dishes[${rowCount}].price"
                            placeholder="価格"
                            required>
                    </td>
                    <td>
                        <input
                            type="text"
                            name="categories[${categoryId}].dishes[${rowCount}].description"
                            placeholder="説明"
                            >
                    </td>
                    <td>
                        <button type="button" class="removeDish">削除</button>
                    </td>
                `;

                // 新しい行をテーブルに追加
                tableBody.appendChild(newRow);
            }
        }
    }
});

document.addEventListener('click', function (e) {
    if (e.target && e.target.classList.contains('removeCategory')) {
        const selectedCategory = document.querySelector('input[name="category"]:checked');

        // ラジオボタンが選択されているか確認
        if (selectedCategory) {
            // 選択されているラジオボタンのvalueを取得
            const selectedCategoryValue = selectedCategory.value;
            const selectedId = "category-" + selectedCategoryValue;

            const selectedDiv = document.getElementById(selectedId);

            if(selectedDiv){
                selectedDiv.remove();
            }

            const labelElement = selectedCategory.closest('label');
            if(labelElement){
                labelElement.remove();
            }

            const selectedCategoryName = document.getElementById("selectedCategoryName");
            selectedCategoryName.textContent = "";
        }
    }

    if (e.target && e.target.classList.contains('addCategory')) {
        const newLabel = document.createElement("label");
        const newId = document.querySelectorAll('#categoryContainer label').length + 1;

        const newRadio = document.createElement("input");
        newRadio.type = "radio";
        newRadio.name = "category";
        newRadio.value = newId;

        const labelCount = document.querySelectorAll('#categoryContainer label').length;
        const newHidden = document.createElement("input");
        newHidden.type = "hidden";
        newHidden.name = "categories[" + labelCount + "].id";
        newHidden.value = newId;

        const newInput = document.createElement("input");
        newInput.type = "text";
        newInput.name = "categories[" + labelCount + "].name";

        newLabel.appendChild(newRadio);
        newLabel.appendChild(newHidden);
        newLabel.appendChild(newInput);

        const categoryContainer = document.getElementById("categoryContainer");
        const lastLabel = categoryContainer.querySelector("label:last-of-type");
        lastLabel.insertAdjacentElement("afterend", newLabel);

        newRadio.addEventListener('change', handleRadioChange);

        const newCategoryHTML = `
            <div id="category-${newId}" class="content" style="display: none;">
                <table>
                    <thead>
                    <tr>
                        <th>料理名</th>
                        <th>価格</th>
                        <th>説明</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr class="dish-row">
                        <input type="hidden" name="categories[${newId-1}].dishes[0].category.id" value="${newId}">
                        <td>
                            <input type="text" name="categories[${newId-1}].dishes[0].name" placeholder="料理名" required>
                        </td>
                        <td>
                            <input type="number" name="categories[${newId-1}].dishes[0].price" placeholder="価格" required>
                        </td>
                        <td>
                            <input type="text" name="categories[${newId-1}].dishes[0].description" placeholder="説明">
                        </td>
                        <td>
                            <button type="button" class="removeDish">削除</button>
                        </td>
                    </tr>
                    </tbody>
                </table>
                <button type="button" class="addDish">料理を追加</button>
            </div>
        `;

        const dishDiv = document.getElementById("dishContents");
        dishDiv.insertAdjacentHTML('beforeend', newCategoryHTML);
    }
});

function handleRadioChange(event) {
    const radio = event.target;
    const selectedValue = radio.value; // 選択されたラジオボタンの値

    const selectedLabel = radio.closest("label");
    const selectedCategoryName = document.getElementById("selectedCategoryName");

    const textInput = selectedLabel.querySelector('input[type="text"]');
    if(textInput){
        selectedCategoryName.textContent = textInput.value;
    }

    // すべての div を非表示にする
    const categoryDivs = document.querySelectorAll('.content');
    categoryDivs.forEach(div => {
        div.style.display = 'none';
    });

    // 選択された値と一致する id を持つ div を表示
    const matchingDiv = document.getElementById('category-' + selectedValue);
    if (matchingDiv) {
        matchingDiv.style.display = 'block';
    }
}

document.addEventListener("DOMContentLoaded", function () {
    // ラジオボタンのリストを取得
    const radioButtons = document.querySelectorAll('input[name="category"]');

    // ラジオボタンの選択イベントを監視
    radioButtons.forEach(radio => {
        radio.addEventListener('change', handleRadioChange);
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
