let initData;

function initForm(data) {
    let jsonString = JSON.stringify(data);
    initData = JSON.parse(jsonString);
}

layui.use(['jquery', 'layer', 'upload', 'excel', 'laytpl', 'element', 'code', "okUtils","okMd5"], function () {
    let $ = layui.jquery;
    let layer = layui.layer;
    let upload = layui.upload;
    let excel = layui.excel;
    let laytpl = layui.laytpl;
    let element = layui.element;
    let okUtils = layui.okUtils;
    let okMd5 = layui.okMd5;

    let storage = window.localStorage;
    storage.skin = "1";
    storage.anim = "5";

    /**
     * 上传excel的处理函数，传入文件对象数组
     * @param  {FileList} files [description]
     * @return {[type]}       [description]
     */
    function uploadExcel(files) {
        try {
            excel.importExcel(files, {
                // 可以在读取数据的同时梳理数据
                fields: {
                    'num':'A',
                    'administrativeDivision':'B',
                    'caseId':'C',
                    'eventId':'D',
                    'reportingTime':'E',
                    'alarmCategory':'F',
                    'alarmType':'G',
                    'alarmSystem':'H',
                    'alarmSubclass':'I',
                    'affiliatedUnit':'J',
                    'jurisdictionUnit':'K',
                    'incidentSite':'L',
                    'reportingContent':'M',
                    'policeUnit':'N',
                    'dispatchForce':'O',
                    'reportingLevel':'P',
                    'informerName':'Q',
                    'informerSex':'R',
                    'alarmRecording':'S',
                    'contactNumber':'T',
                    'receivingUnit':'U',
                    'operatorId':'V',
                    'nameOfPoliceOfficer':'W',
                    'feedbackContent':'X',
                    'processingResults':'Y',
                    'caseState':'Z'
                }
            }, function (data, book) {
                layer.open({
                    type: 1
                    , title: '文件转换结果'
                    , area: ['799px', '399px']
                    , tipsMore: true
                    , maxmin: true
                    , content: laytpl($('#LAY-excel-export-ans').html()).render({data: data, files: files})
                    , btn: ['确定', '取消']
                    , success: function () {
                        element.render('tab')
                        layui.code({})
                        // 处理合并
                        for (var file_index in book) {
                            if (!book.hasOwnProperty(file_index)) {
                                continue
                            }
                            // 遍历每个Sheet
                            for (var sheet_name in book[file_index].Sheets) {
                                if (!book[file_index].Sheets.hasOwnProperty(sheet_name)) {
                                    continue
                                }
                                var sheetObj = book[file_index].Sheets[sheet_name]
                                // 仅在有合并参数时进行操作
                                if (!sheetObj['!merges']) {
                                    continue
                                }
                                // 遍历每个Sheet中每个 !merges
                                for (var merge_index = 0; merge_index < sheetObj['!merges'].length; merge_index++) {
                                    var mergeObj = sheetObj['!merges'][merge_index]
                                    // 每个合并参数的 s.c 表示左上角单元格的列，s.r 表示左上角单元格的行，e.c 表示右下角单元格的列，e.r 表示右下角单元格的行，计算时注意 + 1
                                    $('#table-export-' + file_index + '-' + sheet_name + '-' + mergeObj.s.r + '-' + mergeObj.s.c)
                                        .prop('rowspan', mergeObj.e.r - mergeObj.s.r + 1)
                                        .prop('colspan', mergeObj.e.c - mergeObj.s.c + 1)
                                    for (var r = mergeObj.s.r; r <= mergeObj.e.r; r++) {
                                        for (var c = mergeObj.s.c; c <= mergeObj.e.c; c++) {
                                            // 排除左上角
                                            if (r === mergeObj.s.r && c === mergeObj.s.c) {
                                                continue
                                            }
                                            $('#table-export-' + file_index + '-' + sheet_name + '-' + r + '-' + c).remove()
                                        }
                                    }
                                }
                            }
                        }
                    }, yes: function(index,layero){
                        layer.close(index);
                        //ajax运行处
                        if (data){
                            var sheetStr=Object.keys(data[0])[0];
                            var sheet=data[0][sheetStr]
                            var array=[];
                            for (var i=1;i<sheet.length;i++){
                                array.push(new Case(sheet[i].caseId,sheet[i].administrativeDivision,sheet[i].eventId,sheet[i].reportingTime,sheet[i].alarmCategory,sheet[i].alarmType,sheet[i].alarmSystem,
                                    sheet[i].alarmSubclass,sheet[i].affiliatedUnit,sheet[i].jurisdictionUnit,sheet[i].incidentSite,
                                    sheet[i].reportingContent,sheet[i].policeUnit,sheet[i].dispatchForce,sheet[i].reportingLevel,sheet[i].informerName,sheet[i].informerSex,sheet[i].alarmRecording,
                                    sheet[i].contactNumber,sheet[i].receivingUnit,sheet[i].operatorId,sheet[i].nameOfPoliceOfficer,
                                    sheet[i].feedbackContent,sheet[i].processingResults,sheet[i].caseState));
                            }
                            okUtils.ajax(mainUri+"/case/caseImport", "post", {"jsonArrayCase": JSON.stringify(array)}, true).done(function (response) {
                                layer.msg(response.msg, {
                                    icon:1,
                                    time: 20000, //20s后自动关闭
                                    btn: ['继续添加', '退出'],
                                    yes(index,layero){
                                        layer.close(index);
                                    },btn1(){
                                        parent.layer.close(parent.layer.getFrameIndex(window.name));
                                    }});
                            }).fail(function (error) {
                                console.log(error)
                            });
                        }
                    },btn2 :function (){
                        alert("取消操作")
                    }
                })
            })
        } catch (e) {
            layer.alert(e.message)
        }
    }


    //upload上传实例
    var uploadInst = upload.render({
        elem: '#test10' //绑定元素
        , url: '/upload/' //上传接口（PS:这里不用传递整个 excel）
        , auto: false //选择文件后不自动上传
        , accept: 'file'
        , choose: function (obj) {// 选择文件回调
            var files = obj.pushFile()
            var fileArr = Object.values(files)// 注意这里的数据需要是数组，所以需要转换一下

            // 用完就清理掉，避免多次选中相同文件时出现问题
            for (var index in files) {
                if (files.hasOwnProperty(index)) {
                    delete files[index]
                }
            }
            $('#test10').next().val('');

            uploadExcel(fileArr) // 如果只需要最新选择的文件，可以这样写： uploadExcel([files.pop()])
        }
    });

})