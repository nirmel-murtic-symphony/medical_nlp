from xml.dom import minidom
from sklearn import metrics
from sklearn.tree import DecisionTreeClassifier
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.multiclass import OneVsRestClassifier
from sklearn.linear_model import LogisticRegression
from sklearn import cross_validation
from sklearn.ensemble import RandomForestClassifier,GradientBoostingClassifier
import numpy as np
# from passage.models import RNN
# from passage.updates import Adadelta
# from passage.layers import Embedding, GatedRecurrent, Dense
from sklearn.preprocessing import MultiLabelBinarizer
import vector_support as vs
from sklearn.feature_selection import SelectFromModel
from sklearn.kernel_ridge import KernelRidge
from sklearn.neighbors import KDTree

import warnings
warnings.filterwarnings("ignore")

wrong_class_log_hander = open("Monitor/wrong_log.txt", 'w+')

fname = "data/2007ChallengeTrainData.xml"
docu = minidom.parse(fname)
docs = docu.getElementsByTagName("doc")

x = []
x2 = []
y = []
x_vector_support = []

path_ndata = "java_support/medical_nlp/11.txt"
print(path_ndata)
f_hander = open(path_ndata,"r")
ntexts = f_hander.readlines()

def transfer_multilabel(y_predict, y_text,ml,test_index,y_predict_prob):
    y_predict_new = []
    y_text_new = []
    n_record = y_predict.shape[0]
    for idx in range(0, n_record):
        y_predict_record_prob = y_predict_prob[idx]
        y_predict_record = y_predict[idx]
        y_text_record = y_text[idx]
        find_items = np.where(True == np.logical_and(y_predict_record, y_text_record))
        if len(find_items[0]) > 0:
            # code = ml.inverse_transform(y_text_record)
            # y_predict_new.append(code)
            # y_text_new.append(code)
            code = ml.classes_[find_items[0][0]]
            y_predict_new.append(code)
            y_text_new.append(code)
        else :
            code = ml.classes_[np.where(1 == y_text_record)[0][0]]
            y_text_new.append(code)
            # y_predict_new.append("error")
            # y_text_new.append(ml.inverse_transform(y_text_record))
            if len(np.where(1 == y_predict_record)[0]) > 0:
                code_wrong = ml.classes_[np.where(1 == y_predict_record)[0][0]]
            else:
                code_wrong = detailed_train(ml, y_predict_record_prob)

            y_predict_new.append(code_wrong)
    return y_text_new,y_predict_new

def detailed_train(ml, y_predict_record_prob):
    code = ml.classes_[np.argmax(y_predict_record_prob)]
    return code

idx = 0
for doc in docs:
    codes = doc.getElementsByTagName("code")
    texts = doc.getElementsByTagName("text")

    code_label = []
    for code in codes:
        if code.getAttribute("origin") == "CMC_MAJORITY":
            code_label.append(code.firstChild.data)
    y.append(code_label)

    text_data = ""
    for text in texts:
        text_data = " ".join([text_data, text.firstChild.data])
    x.append(ntexts[idx].decode('utf-8'))
    # x.append(text_data)
    idx = idx + 1


# tv = TfidfVectorizer(x,stop_words='english',  min_df=0.00002)
tv = CountVectorizer(x, strip_accents='ascii', ngram_range=(1, 2), binary=True)
# tv = HashingVectorizer(x,strip_accents='ascii',ngram_range = (1,1), binary = True)
tfidf_train = tv.fit_transform(x)
#tfidf_train = np.concatenate((tfidf_train.todense(),np.array(x_vector_support)),axis=1)
ml = MultiLabelBinarizer()
y_map = ml.fit_transform(y)
y_map = np.array(y_map)


f_scores = []
prec = []
recall = []

for loop_stat in range(0,100):
    scores = []
    report_y_actual = []
    report_y_predict = []
    kf = cross_validation.KFold(tfidf_train.shape[0], n_folds=5, shuffle=True)
    loop = 0
    for train_index, test_index in kf:
        x_train, x_test = tfidf_train[train_index].toarray(), tfidf_train[test_index].toarray()
        y_train, y_test = y_map[train_index], y_map[test_index]

        model = OneVsRestClassifier(LogisticRegression(C=10000))
        model.fit(x_train, y_train)
        y_predict = model.predict(x_test)
        y_predict_prob = model.predict_proba(x_test)
        y_text_new,y_predict_new = transfer_multilabel(y_predict, y_test,ml,test_index,y_predict_prob)
        report_y_predict.extend(y_predict_new)
        report_y_actual.extend(y_text_new)
        loop = loop + 1
        # if loop % 10 == 0:
        #     print(metrics.f1_score(report_y_actual, report_y_predict))

    #m_cls_report = metrics.classification_report(report_y_actual, report_y_predict)
    f_score = metrics.f1_score(report_y_actual, report_y_predict)
    f_scores.append(f_score)
    prec.append(metrics.precision_score(report_y_actual, report_y_predict))
    recall.append(metrics.recall_score(report_y_actual, report_y_predict))
    #print(m_cls_report)

print(prec)
print(recall)
print(f_scores)