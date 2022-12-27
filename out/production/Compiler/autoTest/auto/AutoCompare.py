import os
import subprocess

'''
    _me: 自己的jar包文件
    _other: 对拍模式下另一个人的jar包文件
    _run_with_other: 是否开启对拍模式.
        *: 若开启对拍模式, 自己的输出默认为my_output.txt, 其他人的为output.txt
        *: 对拍模式不支持目标代码运行对拍.
    _test_mips: 是否开启目标代码测试, 默认为True
'''
_me = 'Compiler.jar' #自己的jar包名字
_other = 'cjj.jar'
_run_with_other = False
_test_mips = True


def _execute_myself(_cmd):
    p = subprocess.Popen(_cmd, shell=True)
    _stdout, _stderr = p.communicate()
    return


def _read_from_file(_file):
    _output_list = list()
    with open(_file, 'r') as _output:
        for _line in _output:
            if not _line.strip() == '':
                _output_list.append(_line.strip())
    return _output_list


def _compare(_my_output_list, _other_output_list):
    if not len(_my_output_list) == len(_other_output_list):
        return False, None
    for i in range(len(_my_output_list)):
        if not _my_output_list[i] == _other_output_list[i]:
            return False, i+1
    return True, None


def _standardize_input(_input_file="input.txt"):
    _input_content = []
    with open(_input_file, 'r') as _input:
        for _line in _input:
            _nums = _line.split(" ")
            for _num in _nums:
                if not _num.strip() == "" and not _num.strip() == "\n":
                    _input_content.append(_num.replace("\n", ""))

    with open(_input_file, 'w') as _input:
        for _line in _input_content:
            if not _line.strip() == "\n":
                _input.write(_line + "\n")


def _copy_content_to_file(_source_file, _des_file):
    with open(_source_file, "r", encoding='UTF-8') as _source_content:
        _content = _source_content.read()

    with open(_des_file, "w", encoding='UTF-8') as _des_content:
        _des_content.write(_content)


def _run_test_with_std(test_point, _testfile_dir, _std_dir):
    print("****** Testing test point {} ******".format(test_point))
    _source = _testfile_dir + "testfile{}.txt".format(test_point)
    _input = _testfile_dir + "input{}.txt".format(test_point)
    # try:
    #     _copy_content_to_file(_source, _des_file="testfile.txt")
    #     _copy_content_to_file(_input, _des_file="input.txt")
    # except UnicodeDecodeError:
    #     print("{} unicode error raise!".format(test_point))
    #     return

    _copy_content_to_file(_source, _des_file="testfile.txt")
    _copy_content_to_file(_input, _des_file="input.txt")

    if _test_mips:
        _execute_myself(_cmd='java -jar {} > stdout.txt'.format(_me))
        _standardize_input()
        _command = 'java -jar mars.jar mips.txt < input.txt > output.txt'
        os.system(_command)
        _my_output = _read_from_file("output.txt")[1:]
    else:
        _execute_myself(_cmd='java -jar {} < input.txt'.format(_me))
        _my_output = _read_from_file("output.txt")

    _std_file = _std_dir + "output{}.txt".format(test_point)
    _std_output = _read_from_file(_std_file)

    correct, line = _compare(_my_output, _std_output)

    if not correct:
        print("**************************************")
        print("**************************************")
        print("********  {} fail at line {}  ********".format(test_point, line))
        print("**************************************")
        print("**************************************")
        return False
    else:
        # print("accurate")
        return True


def _run_test_with_other(test_point, _testfile_dir):
    print("****** Testing test point {} ******".format(test_point))
    _source = _testfile_dir + "testfile{}.txt".format(test_point)
    try:
        _copy_content_to_file(_source, _des_file="testfile.txt")
    except UnicodeDecodeError:
        print("{} unicode error raise!".format(test_point))
        return
    _execute_myself(_me)
    _execute_myself(_other)

    _my_output = _read_from_file("my_output.txt")
    _other_output = _read_from_file("output.txt")

    correct, line = _compare(_my_output, _other_output)

    if not correct:
        print("{} fail at line {}".format(test_point, line))
        return False
    else:
        print("accurate")
        return True


def _run_single_test_with_other():
    print("****** Testing test point ******")
    _execute_myself(_me)
    _execute_myself(_other)

    _my_output = _read_from_file("my_output.txt")
    _other_output = _read_from_file("output.txt")

    correct, line = _compare(_my_output, _other_output)

    if not correct:
        print("fail at line {}".format(line))
        return False
    else:
        print("accurate")
        return True


def _run_set(_year=2022, _level='A'):
    global _run_with_other, _config
    print("--------> Test {} {} <--------".format(_year, _level))
    _testfile_dir = 'D:/javacode-OO/Compiler/autoTest/testcase/{}_testcases/full/{}/'.format(_year, _level)
    _std_dir = 'D:/javacode-OO/Compiler/autoTest/testcase/{}_testcases/full/{}/'.format(_year, _level)
    for _test_point in range(1, _config[(_year, _level)] + 1):
        if not _run_with_other:
            _run_test_with_std(test_point=_test_point, _testfile_dir=_testfile_dir, _std_dir=_std_dir)
        else:
            _run_test_with_other(test_point=_test_point, _testfile_dir=_testfile_dir)


def _run_public_test(_limit=94):
    global  _run_with_other
    invalid = [10, 47]
    print("--------> Public Test <--------")
    _testfile_dir = "../testcase/public/"
    _std_dir = "../testcase/public/"
    for _test_point in range(1, 94):
        if _test_point in invalid:
            continue
        _run_test_with_std(test_point=_test_point, _testfile_dir=_testfile_dir, _std_dir=_std_dir)


def _run_error_process():
    global _me

    _error_dir = "../testcase/error/"
    _total = 13
    for i in range(1, _total + 1):
        print("****** Testing test point {} ******".format(i))
        _source = _error_dir + "testfile{}.txt".format(i)
        _copy_content_to_file(_source, _des_file="testfile.txt")
        _execute_myself(_me)

        _my_outputs = _read_from_file("error.txt")
        _std_outputs = _read_from_file(_error_dir + "output{}.txt".format(i))

        correct, line = _compare(_my_outputs, _std_outputs)

        if not correct:
            print("{} fail at line {}".format(i, line))
        else:
            print("accurate")


_years = [2021, 2022]
_levels = ['A', 'B', 'C']
_config = {(2021, 'A'): 26, (2021, 'B'): 27, (2021, 'C'): 29,
           (2022, 'A'): 30, (2022, 'B'): 30, (2022, 'C'): 30}


'''
    _run_set(_year, _level): 自动评测_year年的_level样例.
    _run_public_test: 自动评测编译大赛辅助样例 (该样例为自己手动修改,不一定与官方的保持一致).
    若使用该自动评测, 直接对_me进行更改即可, 对拍模式是和官方给的输出进行对拍.
'''

if __name__ == "__main__":
    _run_set(2022, 'A')
    _run_set(2022, 'B')
    _run_set(2022, 'C')
    _run_set(2021, 'A')
    _run_set(2021, 'B')
    _run_set(2021, 'C')

    # 由于某些样例不合法, 故直接ban掉了几个点. #
    # 编译大赛测试点
    #_run_public_test()
    #_run_error_process()
    # _year, _level = 2022, 'A'
    # _testfile_dir = '../testcase/{}_testcases/full/{}/'.format(_year, _level)
    # _std_dir = '../testcase/{}_testcases/full/{}/'.format(_year, _level)
    # _run_test_with_std(14, _testfile_dir, _std_dir)

    # _testfile_dir = '../testcase/public/'
    # _std_dir = '../testcase/public'
    # _run_test_with_std(69, _testfile_dir, _std_dir)

# 5 23 31 50 57

