export const newAccountFakeObject = {
    isNew: true,
    isSecret: true,
}


export const emptyAccount = {
    'userRole': '',
    'account': {
        'id': 0,
        'name': '',
        'dateCreated': ''
    },
    'userList': [],
    'goalList': [],
    'receiptList': [],
    'weight': 0.0
};

export const EmptyReceipt = {
    receiptId: '',
    date: '',
    shop: {
        name: '',
    },
    total: 0,
    positionList: [
        {
            positionId: '',
            name: '',
            price: '',
            category: {
                categoryId: '',
                categoryName: '',
            },
        }
    ]
}


export const EmptyFullAccount = {
    'accountUser': {
        'account': {
            'id': null,
            'name': null,
            'dateCreated': null
        },
        'user': {
            'id': null,
            'identity': {
                'id': null,
                'surname': null,
                'name': null
            },
            'login': null,
            'role': null
        },
        'weight': null,
        'permission': null,
        'id': null
    },
    'receiptList': [
        {
            'receiptId': '',
            'date': '',
            'account': {
                'account': {
                    'id': '',
                    'name': '',
                    'dateCreated': ''
                },
                'user': {
                    'id': '',
                    'identity': {
                        'id':0,
                        'surname': '',
                        'name': ''
                    },
                    'login': '',
                    'role': ''
                },
                'weight':0,
                'permission': '',
                'id': 0
            },
            'shop': {
                'name': ''
            },
            'total': 0,
            'positionList': []
        }
    ]
}

export const ACCOUNT_ROLES = {
    ACCOUNT_USER : "Пользователь ",
    ACCOUNT_CREATOR: "Создатель",
    ACCOUNT_ADMIN : "Администратор"
}


export const DATE_PRECISIONS = {
    DAY: "DAY",
    WEEK : "WEEK",
    MONTH: "MONTH",
    YEAR: "YEAR"
}






