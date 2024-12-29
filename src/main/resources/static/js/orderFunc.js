document.addEventListener('click', function (e) {
    if (e.target && e.target.classList.contains('orderDish')) {
        const tr = e.target.closest('tr');

        const dishId = tr.querySelector('.dish-id').value;
        const dishName = tr.querySelector('.dish-name').innerText; // 料理名
        const dishPrice = tr.querySelector('.dish-price').innerText; // 価格
        const dishDescription = tr.querySelector('.dish-description').innerText; // 詳細説明

        // モーダルの内容を更新
        document.getElementById('modal-dish-id').value = dishId;
        document.getElementById('modal-dish-name').innerText = dishName;
        document.getElementById('modal-dish-price').innerText = dishPrice;
        document.getElementById('modal-dish-description').innerText = dishDescription;

        // モーダルを表示
        document.getElementById('order-modal').style.display = 'block';
    }

    if (e.target && e.target.classList.contains('delete-btn')) {
        const tr = e.target.closest('tr');  // 削除ボタンの親行を取得
        if (tr) {
            tr.remove();  // 親行を削除
        }
    }

    if (e.target && e.target.type === 'submit') {
        const form = e.target.closest('form');
        if (form) {
            form.submit();  // 明示的にフォームを送信
        }
    }
});

function closeOrderModal() {
    // モーダルを非表示にする
    document.getElementById('order-modal').style.display = 'none';
}

function submitOrder() {
    const quantity = document.getElementById('quantity').value;
    if (quantity && quantity > 0) {
        const cartDiv = document.getElementById("shopping-cart");
        if(cartDiv){
        const tableBody = cartDiv.querySelector('tbody');
            if(tableBody){
                const dishId = document.getElementById('modal-dish-id').value;
                const dishName = document.getElementById('modal-dish-name').innerText;
                const rowCount = tableBody.querySelectorAll('.dish-row').length;

                const newRow = document.createElement('tr');
                newRow.classList.add('dish-row');
                newRow.innerHTML = `
                    <input
                        type="hidden"
                        name="orders[${dishId}]"
                        value="${quantity}"
                    >
                    <td>
                        <input
                            type="text"
                            value="${dishName}"
                            readonly>
                    </td>
                    <td>
                        <input
                            type="number"
                            value="${quantity}"
                            readonly>
                    </td>
                    <td>
                        <button type="button" class="delete-btn">削除</button>
                    </td>
                `;

                // 新しい行をテーブルに追加
                tableBody.appendChild(newRow);
            }
        }

        closeOrderModal();
    } else {
        alert('正しい個数を入力してください');
    }
}

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